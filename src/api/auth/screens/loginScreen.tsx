import React, { useState } from 'react';
import { View, Text, TextInput, Button, Alert, StyleSheet } from 'react-native';
import { userApi } from '../api/userApi';
import type { LoginRequestDto } from '../types/User';
import { useNavigation } from '@react-navigation/native';

const LoginScreen = () => {
  const navigation = useNavigation();
  const [form, setForm] = useState<LoginRequestDto>({
    email: '',
    password: '',
    userRole: 'IND',
    loginType: 'LOCAL',
  });

  const handleLogin = async () => {
    try {
      const response = await userApi.login(form);
      console.log('로그인 성공:', response.data);
      Alert.alert('로그인 성공');
      navigation.navigate('Home' as never);
    } catch (error) {
      console.error(error);
      Alert.alert('로그인 실패', '이메일 또는 비밀번호를 확인해주세요');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>로그인</Text>
      <TextInput placeholder="이메일" style={styles.input} value={form.email} onChangeText={text => setForm({ ...form, email: text })} />
      <TextInput placeholder="비밀번호" style={styles.input} secureTextEntry value={form.password} onChangeText={text => setForm({ ...form, password: text })} />
      <Button title="로그인" onPress={handleLogin} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, justifyContent: 'center' },
  title: { fontSize: 24, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  input: { borderWidth: 1, borderColor: '#ccc', borderRadius: 6, padding: 10, marginBottom: 12 },
});

export default LoginScreen;