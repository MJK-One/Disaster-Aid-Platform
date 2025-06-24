import React, { useState } from 'react';
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

      if (!accessToken) {
        throw new Error('accessToken이 존재하지 않습니다.');
      }

      await AsyncStorage.setItem('accessToken', accessToken);
      console.log('🟢 accessToken 저장됨:', accessToken);

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

      Alert.alert('로그인 성공');
      navigation.navigate('Welcome' as never);
    } catch (error) {
      console.error(error);
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
