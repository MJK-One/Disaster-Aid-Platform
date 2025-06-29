import React, { useState, useEffect } from 'react';
import {
  View, Text, TextInput, StyleSheet,
  Alert, TouchableOpacity, ActivityIndicator,
} from 'react-native';
import { useCurrentLocation } from '../../location/hooks/useCurrentLocation'; // 네이티브 위치 추적 훅
import { createReport } from '../api/report';

enum DisasterType {
  EARTHQUAKE = 'EARTHQUAKE',
  FLOOD = 'FLOOD',
  TYPHOON = 'TYPHOON',
  WILDFIRE = 'WILDFIRE',
  LANDSLIDE = 'LANDSLIDE',
  POWER_OUTAGE = 'POWER_OUTAGE',
  TERROR_ATTACK = 'TERROR_ATTACK',
  BUILDING_COLLAPSE = 'BUILDING_COLLAPSE'
}

const disasterTypeNames: Record<DisasterType, string> = {
  [DisasterType.EARTHQUAKE]: '지진',
  [DisasterType.FLOOD]: '홍수',
  [DisasterType.TYPHOON]: '태풍',
  [DisasterType.WILDFIRE]: '산불',
  [DisasterType.LANDSLIDE]: '산사태',
  [DisasterType.POWER_OUTAGE]: '정전',
  [DisasterType.TERROR_ATTACK]: '테러',
  [DisasterType.BUILDING_COLLAPSE]: '건물 붕괴'
};

const B4_ORANGE = '#FF6B00';
const B4_ORANGE_LIGHT = '#FFD4B3';

const ReportScreen: React.FC = () => {
  const [selectedType, setSelectedType] = useState<DisasterType | null>(null);
  const [description, setDescription] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 네이티브 백그라운드 위치 추적 훅 사용
  const { latitude, longitude, si, gu, loading: isLocating } = useCurrentLocation();

  const handleReport = async () => {
    if (!selectedType || !description.trim() || !si || latitude == null || longitude == null) {
      Alert.alert('오류', '재난 유형, 설명, 위치 정보가 모두 필요합니다.');
      return;
    }

    setIsSubmitting(true);
    try {
      const payload = {
        disasterType: selectedType,
        description,
        si,
        gu: gu || '없음',
        latitude,
        longitude,
      };

      const response = await createReport(payload);
      Alert.alert('신고 완료', `재난 유형: ${response.disasterType}`);
      resetForm();
    } catch (err) {
      console.error('❌ 신고 실패:', err);
      Alert.alert('신고 실패', '서버 요청 중 문제가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const resetForm = () => {
    setSelectedType(null);
    setDescription('');
  };

  if (isLocating) {
    return (
      <View style={styles.container}>
        <ActivityIndicator size="large" color={B4_ORANGE} />
        <Text style={{ marginTop: 20 }}>위치 정보를 가져오는 중...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.header}>재난 신고</Text>

      <Text style={styles.subheader}>재난 유형 선택</Text>
      <View style={styles.typeContainer}>
        {Object.entries(disasterTypeNames).map(([key, label]) => (
          <TouchableOpacity
            key={key}
            onPress={() => setSelectedType(key as DisasterType)}
            style={[
              styles.typeButton,
              selectedType === key && styles.typeButtonSelected
            ]}
          >
            <Text style={{
              color: selectedType === key ? 'white' : 'black',
              fontWeight: '600'
            }}>
              {label}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={styles.locationLabel}>📍 위치: {si} {gu}</Text>

      <TextInput
        style={styles.input}
        multiline
        placeholder="상황 설명을 입력하세요 (최대 1000자)"
        value={description}
        maxLength={1000}
        onChangeText={setDescription}
      />
      <Text style={styles.charCount}>{description.length}/1000</Text>

      <TouchableOpacity
        style={[styles.submitButton, isSubmitting && styles.buttonDisabled]}
        onPress={handleReport}
        disabled={isSubmitting}
      >
        <Text style={styles.submitButtonText}>
          {isSubmitting ? '신고 접수 중...' : '긴급 신고하기'}
        </Text>
      </TouchableOpacity>
    </View>
  );
};

export default ReportScreen;

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#f9f9f9', justifyContent: 'center' },
  header: { fontSize: 24, fontWeight: 'bold', marginBottom: 20 },
  subheader: { fontSize: 16, fontWeight: '600', marginBottom: 10 },
  typeContainer: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  typeButton: {
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    margin: 4,
    backgroundColor: '#fff',
  },
  typeButtonSelected: {
    backgroundColor: B4_ORANGE_LIGHT,
    borderColor: B4_ORANGE,
  },
  locationLabel: {
    fontSize: 14,
    marginTop: 10,
    marginBottom: 5,
    color: '#555'
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 10,
    minHeight: 100,
    marginTop: 16,
    backgroundColor: 'white',
  },
  charCount: {
    textAlign: 'right',
    fontSize: 12,
    color: '#888',
    marginBottom: 12,
  },
  submitButton: {
    backgroundColor: B4_ORANGE,
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 16,
    alignItems: 'center',
    marginTop: 20
  },
  submitButtonText: {
    color: '#fff',
    fontWeight: 'bold',
    fontSize: 16
  },
  buttonDisabled: {
    opacity: 0.6
  }
});
