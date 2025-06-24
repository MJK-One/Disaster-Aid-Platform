import axios from 'axios';
import { Platform } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

// ✅ PC 로컬 IP
const localIP = '192.168.0.12';

const baseURL = Platform.OS === 'android'
  ? `http://${localIP}:8080/api`
  : 'http://localhost:8080/api';

const axiosInstance = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ✅ 요청 인터셉터 - accessToken 자동 삽입
axiosInstance.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ✅ 응답 인터셉터 - 401 시 refreshToken으로 accessToken 재발급
axiosInstance.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = await AsyncStorage.getItem('refreshToken');
        if (!refreshToken) throw new Error('refreshToken 없음');

        const res = await axios.post(`${baseURL}/auth/reissue`, { refreshToken });
        const newAccessToken = res.data?.payload?.accessToken;
        if (!newAccessToken) throw new Error('accessToken 재발급 실패');

        await AsyncStorage.setItem('accessToken', newAccessToken);
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        return axiosInstance(originalRequest); // 원래 요청 재시도
      } catch (reissueError) {
        console.error('🔴 Token 재발급 실패:', reissueError);
        // 필요시 AsyncStorage.removeItem('accessToken'); AsyncStorage.removeItem('refreshToken');
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;