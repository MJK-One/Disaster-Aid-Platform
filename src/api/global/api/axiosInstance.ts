// 📁 src/api/axiosInstance.ts
import axios from 'axios';
import { Platform } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

// 실제 로컬 서버 IP 주소 (Android 실기기에서 테스트할 경우 PC의 IP)
const localIP = '192.168.45.70'; // ← 본인의 PC IP로 수정하세요

// baseURL 설정: Android 실기기/에뮬레이터, iOS 등 분기
const baseURL =
  Platform.OS === 'android'
    ? `http://${localIP}:8080/api` // Android (실기기 포함)
    : 'http://localhost:8080/api'; // iOS 시뮬레이터 등

console.log('🌐 [Axios] BaseURL:', baseURL);

// Axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: accessToken 자동 삽입
axiosInstance.interceptors.request.use(async (config) => {
  try {
    const token = await AsyncStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  } catch (e) {
    console.warn('⚠️ [Axios] accessToken 불러오기 실패:', e);
  }
  return config;
});

// 응답 인터셉터: accessToken 만료 시 refreshToken으로 재발급 시도
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

        console.log('🔄 [Axios] accessToken 재발급 성공');
        return axiosInstance(originalRequest); // 원래 요청 재시도
      } catch (reissueError) {
        console.error('🔴 [Axios] 토큰 재발급 실패:', reissueError);
        await AsyncStorage.multiRemove(['accessToken', 'refreshToken']);
        // TODO: 로그인 화면으로 이동 처리 필요 시 여기에 작성
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
