// 📁 src/api/axiosInstance.ts
import axios from 'axios';
import { Platform } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

// 📡 실제 로컬 서버 IP 주소 (기기에서 테스트 시 필요)
const localIP = '192.168.45.70'; // ← 필요시 본인 PC의 IP로 수정

// ✅ baseURL 설정: 에뮬레이터 / 실제 기기 / iOS
const baseURL =
  Platform.OS === 'android'
    ? 'http://10.0.2.2:8080/api' // Android 에뮬레이터 전용
    : `http://${localIP}:8080/api`; // iOS 시뮬레이터 또는 실제 디바이스용

console.log('🌐 [Axios] BaseURL:', baseURL);

// ✅ Axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ✅ 요청 인터셉터: accessToken 자동 삽입
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

// ✅ 응답 인터셉터: accessToken 만료 시 재발급
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

        console.log('🔄 accessToken 재발급 성공');
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
