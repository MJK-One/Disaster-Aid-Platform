import React, { useState, useEffect } from 'react';
import { View, Text, Button, StyleSheet, ActivityIndicator } from 'react-native';
import { getCurrentLocation } from '../utils/location';
import KakaoMapView from '../components/KakaoMapComponent';
import { shelterApi } from '../api/shelterApi';
import { reportApi } from '../api/reportApi';
import type { ShelterDto, DisasterDto } from '../types/Map';
import { useNavigation, NavigationProp } from '@react-navigation/native';
import type { RootStackParamList } from '../../../navigation/RootNavigator'; 

const MainScreen = () => {
  const [loading, setLoading] = useState(false);
  const [latitude, setLatitude] = useState<number | null>(null);
  const [longitude, setLongitude] = useState<number | null>(null);
  const [shelters, setShelters] = useState<ShelterDto[]>([]);
  const [disasters, setDisasters] = useState<DisasterDto[]>([]);

  const navigation = useNavigation<NavigationProp<RootStackParamList>>();

  useEffect(() => {
    (async () => {
      setLoading(true);
      const location = await getCurrentLocation();
      if (location) {
        setLatitude(location.latitude);
        setLongitude(location.longitude);
      }
      setLoading(false);
    })();
  }, []);

  const fetchShelters = async () => {
    if (latitude === null || longitude === null) return;
    setLoading(true);
    try {
      const data = await shelterApi.getNearbyShelters(latitude, longitude, 1000);
      setShelters(data);
      setDisasters([]);
    } catch (error) {
      console.error('대피소 불러오기 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchDisasters = async () => {
    if (latitude === null || longitude === null) return;
    setLoading(true);
    try {
      const data = await reportApi.getNearbyDisasters(latitude, longitude, 1000);
      setDisasters(data);
      setShelters([]);
    } catch (error) {
      console.error('재난 정보 불러오기 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const goToReportScreen = () => {
    navigation.navigate('ReportScreen');
  };

  if (loading && (latitude === null || longitude === null)) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#FF6B00" />
        <Text>현재 위치를 가져오는 중입니다...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>
        현재 위치: {latitude !== null ? latitude.toFixed(4) : '-'}, {longitude !== null ? longitude.toFixed(4) : '-'}
      </Text>

      <View style={styles.buttonRow}>
        <Button title="대피소 보기" onPress={fetchShelters} color="#FF6B00" />
        <Button title="재난 정보 보기" onPress={fetchDisasters} color="#FF6B00" />
      </View>

      <View style={styles.mapContainer}>
        {latitude !== null && longitude !== null && (
          <KakaoMapView
            latitude={latitude}
            longitude={longitude}
            shelters={shelters}
            disasters={disasters}
          />
        )}
      </View>

      <View style={styles.reportButtonContainer}>
        <Button title="신고하기" onPress={goToReportScreen} color="#FF6B00" />
      </View>
    </View>
  );
};

export default MainScreen;

const styles = StyleSheet.create({
  container: { flex: 1, padding: 10, backgroundColor: '#fff' },
  title: { fontSize: 16, fontWeight: 'bold', marginBottom: 10 },
  buttonRow: { flexDirection: 'row', justifyContent: 'space-around', marginBottom: 10 },
  mapContainer: { flex: 1 },
  reportButtonContainer: {
    marginTop: 10,
    paddingHorizontal: 20,
    marginBottom: 20,
  },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center' },
});
