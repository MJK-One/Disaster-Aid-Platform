// 📁 src/api/axiosInstance.ts
import axios from 'axios';
import { Platform } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

// ✅ 상황별 baseURL 자동 분기
const localIP = '192.168.254.137';
const emulatorURL = 'http://10.0.2.2:8080/api';
const localURL = `http://${localIP}:8080/api`;

const baseURL =
  Platform.OS === 'android'
    ? __DEV__
      ? localURL // ✅ 실제 디바이스에서 개발 중
      : emulatorURL // ✅ 개발자 실수 대비 fallback
    : 'http://localhost:8080/api';

const axiosInstance = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ✅ 요청 인터셉터: accessToken 자동 삽입
axiosInstance.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ✅ 응답 인터셉터: accessToken 만료 시 refreshToken으로 재발급
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
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
        await AsyncStorage.multiRemove(['accessToken', 'refreshToken']);
        // 필요시 로그인 페이지로 강제 이동 처리
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
