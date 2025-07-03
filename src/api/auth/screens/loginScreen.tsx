import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  Alert,
  StyleSheet,
  TouchableOpacity,
  Image,
} from 'react-native';
import { userApi } from '../api/userApi';
import type { LoginRequestDto } from '../types/User';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { setJwtToken } from '../../../nativeModules/JwtModule';
import { requestPushPermission } from '../../alert/fcm/fcmPermissions';
import { getFcmToken } from '../../alert/fcm/fcmTokenManager';
import { sendDeviceInfoToServer } from '../../alert/fcm/sendDeviceInfo';
import { startLocationTrackingService } from '../../location/hooks/startLocationService'; // 추가

const LoginScreen = () => {
  const navigation = useNavigation();
  const [form, setForm] = useState<LoginRequestDto>({
    email: '',
    password: '',
    loginType: 'LOCAL',
  });

  // 자동 로그인 시도 (앱 시작 시 토큰 체크)
  useEffect(() => {
    const tryAutoLogin = async () => {
      try {
        const savedToken = await AsyncStorage.getItem('accessToken');
        if (savedToken) {
          setJwtToken(savedToken);
          // 위치 추적 서비스 시작
          startLocationTrackingService();

          // FCM 토큰 갱신 및 서버 전송
          const permissionGranted = await requestPushPermission();
          if (permissionGranted) {
            const token = await getFcmToken();
            if (token) await sendDeviceInfoToServer(token);
          }

          navigation.navigate('MainScreen' as never);
        }
      } catch (e) {
        console.log('자동 로그인 실패:', e);
      }
    };
    tryAutoLogin();
  }, []);

  const handleLogin = async () => {
    try {
      const response = await userApi.login(form);
      const accessToken = response.data?.payload?.accessToken;
      const refreshToken = response.data?.payload?.refreshToken;

      if (!accessToken || !refreshToken) {
        throw new Error('토큰이 누락되었습니다.');
      }

      // ✅ 토큰을 모두 저장 (동기적으로 완료)
      await AsyncStorage.multiSet([
        ['accessToken', accessToken],
        ['refreshToken', refreshToken],
      ]);

      console.log('🟢 accessToken 저장됨:', accessToken);
      console.log('🟢 refreshToken 저장됨:', refreshToken);

      // ✅ FCM 등록 흐름
      const permissionGranted = await requestPushPermission();
      if (permissionGranted) {
        const token = await getFcmToken();
        console.log('🟢 FCM 토큰:', token);
        if (token) {
          const success = await sendDeviceInfoToServer(token);
          if (!success) console.warn('[FCM] 서버 전송 실패');
        }
      }

      // 위치 추적 서비스 시작
      startLocationTrackingService();

      // ✅ 저장 완료 이후에 네비게이션 이동
      Alert.alert('로그인 성공');
      navigation.navigate('MainScreen' as never);
    } catch (error) {
      console.error('❌ 로그인 실패:', error);
      Alert.alert('로그인 실패', '이메일 또는 비밀번호를 확인해주세요');
    }
  };


  return (
    <View style={styles.container}>
      <Image source={require('../../../img/b4b4.png')} style={styles.logo} />
      <TextInput
        placeholder="이메일"
        style={styles.input}
        value={form.email}
        onChangeText={text => setForm({ ...form, email: text })}
      />
      <TextInput
        placeholder="비밀번호"
        style={styles.input}
        secureTextEntry
        value={form.password}
        onChangeText={text => setForm({ ...form, password: text })}
      />
      <TouchableOpacity style={styles.loginButton} onPress={handleLogin}>
        <Text style={styles.loginButtonText}>로그인</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={styles.signUpLink}
        onPress={() => navigation.navigate('SignUp' as never)}
      >
        <Text style={styles.signUpText}>회원가입</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, justifyContent: 'center' },
  logo: { width: 120, height: 120, resizeMode: 'contain', alignSelf: 'center' },
  input: { borderWidth: 1, borderColor: '#ccc', borderRadius: 6, padding: 10, marginBottom: 12 },
  loginButton: {
    backgroundColor: '#f26522',
    paddingVertical: 12,
    borderRadius: 6,
    alignItems: 'center',
    marginTop: 10,
  },
  loginButtonText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  signUpLink: { marginTop: 16, alignItems: 'center' },
  signUpText: { color: '#f26522', fontWeight: '600' },
});

export default LoginScreen;
