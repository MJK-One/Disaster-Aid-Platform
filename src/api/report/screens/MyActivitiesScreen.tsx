// src/screens/MyActivitiesScreen.tsx
import React from 'react';
import { View, TouchableOpacity, Text, StyleSheet } from 'react-native';
import { useNavigation } from '@react-navigation/native';

export default function MyActivitiesScreen() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('ReportList' as never)}
      >
        <Text style={styles.buttonText}>내 신고 목록</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.button}
        onPress={() => {/* 출석 화면으로 네비게이트 */}}
      >
        <Text style={styles.buttonText}>출석</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.button}
        onPress={() => {/* 참가자 화면으로 네비게이트 */}}
      >
        <Text style={styles.buttonText}>참가자</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f9f9f9',
  },
  button: {
    width: '80%',
    paddingVertical: 15,
    marginVertical: 10,
    backgroundColor: '#fff',
    borderRadius: 8,
    alignItems: 'center',
    elevation: 2,
  },
  buttonText: {
    fontSize: 18,
    fontWeight: '600',
  },
});
