import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  Button,
  Alert,
  StyleSheet,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { userApi } from '../api/userApi';
import type { SignUpRequestDto, UserRole } from '../types/User';
import { useNavigation } from '@react-navigation/native';

const SignUpScreen = () => {
  const navigation = useNavigation();

  const [form, setForm] = useState<SignUpRequestDto>({
    email: '',
    password: '',
    nickname: '',
    phoneNumber: '',
    si: '',
    userRole: 'IND',
    loginType: 'LOCAL',
  });

  const handleSubmit = async () => {
    try {
      await userApi.signUp(form);
      Alert.alert('회원가입 성공');
      navigation.navigate('Login' as never);
    } catch (error) {
      console.error(error);
      Alert.alert('회원가입 실패', '입력값을 다시 확인해주세요');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>회원가입</Text>
      <TextInput
        placeholder="이메일"
        style={styles.input}
        value={form.email}
        onChangeText={(text) => setForm({ ...form, email: text })}
      />
      <TextInput
        placeholder="비밀번호"
        style={styles.input}
        secureTextEntry
        value={form.password}
        onChangeText={(text) => setForm({ ...form, password: text })}
      />
      <TextInput
        placeholder="닉네임"
        style={styles.input}
        value={form.nickname}
        onChangeText={(text) => setForm({ ...form, nickname: text })}
      />
      <TextInput
        placeholder="전화번호"
        style={styles.input}
        keyboardType="phone-pad"
        value={form.phoneNumber}
        onChangeText={(text) => setForm({ ...form, phoneNumber: text })}
      />
      <TextInput
        placeholder="지역 (예: 서울)"
        style={styles.input}
        value={form.si}
        onChangeText={(text) => setForm({ ...form, si: text })}
      />
      <Picker
        selectedValue={form.userRole}
        onValueChange={(itemValue: UserRole) => setForm({ ...form, userRole: itemValue })}
        style={styles.input}
      >
        <Picker.Item label="개인 (IND)" value="IND" />
        <Picker.Item label="민간단체 (NGO)" value="NGO" />
        <Picker.Item label="공공기관 (GOV)" value="GOV" />
      </Picker>

      <Button title="회원가입" onPress={handleSubmit} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, justifyContent: 'center' },
  title: { fontSize: 24, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 6,
    padding: 10,
    marginBottom: 12,
  },
});

export default SignUpScreen;
