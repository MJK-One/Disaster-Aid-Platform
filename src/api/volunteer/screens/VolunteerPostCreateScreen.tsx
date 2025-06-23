import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  Button,
  Alert,
  StyleSheet,
  ScrollView,
} from 'react-native';
import axiosInstance from '../../global/api/axiosInstance';
import { useNavigation } from '@react-navigation/native';

const VolunteerPostCreateScreen = () => {
  const navigation = useNavigation();
  const [form, setForm] = useState({
    title: '',
    content: '',
    category: 'RECRUITMENT', // 무조건 모집
    totalCapacity: '',
    teamSize: '',
    location: {
      placeName: '',
      latitude: '',
      longitude: '',
    },
    attendancePolicy: {
      checkinStart: '',
      checkinEnd: '',
      allowedRadiusM: '',
      minStayMinutes: '',
    },
  });

  const handleSubmit = async () => {
    try {
      const payload = {
        ...form,
        totalCapacity: parseInt(form.totalCapacity),
        teamSize: parseInt(form.teamSize),
        location: {
          ...form.location,
          latitude: parseFloat(form.location.latitude),
          longitude: parseFloat(form.location.longitude),
        },
        attendancePolicy: {
          ...form.attendancePolicy,
          allowedRadiusM: parseInt(form.attendancePolicy.allowedRadiusM),
          minStayMinutes: parseInt(form.attendancePolicy.minStayMinutes),
        },
      };

      await axiosInstance.post('/post', payload); // 백엔드 URL 경로 주의
      Alert.alert('✅ 모집글이 등록되었습니다.');
      navigation.goBack();
    } catch (error) {
      console.error('❌ 작성 실패:', error);
      Alert.alert('작성 실패', '입력값을 다시 확인해주세요');
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>모집글 작성</Text>
      <TextInput placeholder="제목" style={styles.input} value={form.title} onChangeText={v => setForm({ ...form, title: v })} />
      <TextInput placeholder="내용" style={styles.input} value={form.content} onChangeText={v => setForm({ ...form, content: v })} />
      <TextInput placeholder="총 인원" style={styles.input} keyboardType="number-pad" value={form.totalCapacity} onChangeText={v => setForm({ ...form, totalCapacity: v })} />
      <TextInput placeholder="팀당 인원" style={styles.input} keyboardType="number-pad" value={form.teamSize} onChangeText={v => setForm({ ...form, teamSize: v })} />

      <TextInput placeholder="출석 시작 시간 (예: 2025-06-01T09:00)" style={styles.input} value={form.attendancePolicy.checkinStart} onChangeText={v => setForm({ ...form, attendancePolicy: { ...form.attendancePolicy, checkinStart: v } })} />
      <TextInput placeholder="출석 종료 시간 (예: 2025-06-01T12:00)" style={styles.input} value={form.attendancePolicy.checkinEnd} onChangeText={v => setForm({ ...form, attendancePolicy: { ...form.attendancePolicy, checkinEnd: v } })} />
      <TextInput placeholder="출석 반경 (미터)" style={styles.input} keyboardType="number-pad" value={form.attendancePolicy.allowedRadiusM} onChangeText={v => setForm({ ...form, attendancePolicy: { ...form.attendancePolicy, allowedRadiusM: v } })} />
      <TextInput placeholder="최소 출석 시간 (분)" style={styles.input} keyboardType="number-pad" value={form.attendancePolicy.minStayMinutes} onChangeText={v => setForm({ ...form, attendancePolicy: { ...form.attendancePolicy, minStayMinutes: v } })} />

      <TextInput placeholder="장소명" style={styles.input} value={form.location.placeName} onChangeText={v => setForm({ ...form, location: { ...form.location, placeName: v } })} />
      <TextInput placeholder="위도 (예: 37.5665)" style={styles.input} keyboardType="decimal-pad" value={form.location.latitude} onChangeText={v => setForm({ ...form, location: { ...form.location, latitude: v } })} />
      <TextInput placeholder="경도 (예: 126.978)" style={styles.input} keyboardType="decimal-pad" value={form.location.longitude} onChangeText={v => setForm({ ...form, location: { ...form.location, longitude: v } })} />

      <Button title="등록하기" onPress={handleSubmit} />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { padding: 20 },
  title: { fontSize: 22, fontWeight: 'bold', marginBottom: 12 },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 10,
    borderRadius: 6,
    marginBottom: 10,
  },
});

export default VolunteerPostCreateScreen;
