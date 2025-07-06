// 📁 src/api/axiosInstance.ts
import axios from 'axios';
import { Platform } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

// ✅ 환경별 baseURL 분기
// Android 에뮬레이터 → 로컬 서버 접근 시 반드시 10.0.2.2 사용
const emulatorURL = 'http://10.0.2.2:8080/api';

// ✅ 실제 기기에서 테스트할 경우, PC의 로컬 IP 주소를 명시
const localIP = '192.168.0.101'; // 예: 본인 PC IP로 교체
const localURL = `http://${localIP}:8080/api`;

// ✅ baseURL 설정 (에뮬레이터 or 실제 기기 분기)
const baseURL =
  Platform.OS === 'android'
    ? emulatorURL // 항상 에뮬레이터면 10.0.2.2
    : 'http://localhost:8080/api'; // iOS 시뮬레이터 등

console.log('🌐 Axios BaseURL:', baseURL);

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

// ✅ 응답 인터셉터 - 401 발생 시 refreshToken으로 accessToken 재발급 시도
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // accessToken 만료 → 재발급 로직
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
        // 필요 시: 로그인 화면으로 이동 처리
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
