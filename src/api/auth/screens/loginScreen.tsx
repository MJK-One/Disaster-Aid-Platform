import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  Alert,
  StyleSheet,
  TouchableOpacity,
  Image,
  Linking,
} from 'react-native';
import { userApi } from '../api/userApi';
import type { LoginRequestDto } from '../types/User';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { requestPushPermission } from '../../alert/fcm/fcmPermissions';
import { getFcmToken } from '../../alert/fcm/fcmTokenManager';
import { sendDeviceInfoToServer } from '../../alert/fcm/sendDeviceInfo';

const LoginScreen = () => {
  const navigation = useNavigation();
  const [form, setForm] = useState<LoginRequestDto>({
    email: '',
    password: '',
    loginType: 'LOCAL',
  });

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

      // ✅ 저장 완료 이후에 네비게이션 이동
      Alert.alert('로그인 성공');
      navigation.navigate('Welcome' as never);

    } catch (error) {
      console.error('❌ 로그인 실패:', error);
      Alert.alert('로그인 실패', '이메일 또는 비밀번호를 확인해주세요');
    }
  };

  const handleKakaoLogin = () => {
    Linking.openURL('http://10.0.2.2:8080/api/oauth2/authorization/kakao');
    // ↳ 실제 배포 시엔 your-domain.com 으로 교체
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

      {/* ✅ 소셜 로그인 영역 */}
      <View style={styles.socialContainer}>
        <TouchableOpacity style={styles.kakaoButton} onPress={handleKakaoLogin}>
          <Image
            source={require('../../../img/kakao_icon.png')}
            style={styles.kakaoIcon}
          />
        </TouchableOpacity>
      </View>

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
    height : 53
  },
  loginButtonText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  signUpLink: { marginTop: 10, alignItems: 'center' },
  signUpText: { color: '#f26522', fontWeight: '600' },

  // 👇 소셜 로그인 스타일
  socialContainer: {marginTop: 10, alignItems: 'center' },
  kakaoButton: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  kakaoIcon: {
    marginLeft: '2%',
    width: '100%',
    height : 70,
    marginRight: 8,
    resizeMode: 'contain',
  },
  kakaoText: {
    color: '#000000',
    fontWeight: 'bold',
    fontSize: 14,
  },
});

export default LoginScreen;
