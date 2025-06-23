import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TextInput,
  TouchableOpacity,
} from 'react-native';
import { VolunteerPost } from '../types/Post';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { jwtDecode } from 'jwt-decode';
import { useNavigation } from '@react-navigation/native';

const mockPosts: VolunteerPost[] = [
  {
    id: 1,
    title: 'XX구 산불 피해 자원 봉사자 모집',
    organizationName: '민간단체1',
    createdAt: '2025.03.27 10:13',
    status: '모집',
    participants: 1,
    capacity: 100,
  },
  {
    id: 2,
    title: 'XX구 산불 피해 자원 봉사자 모집',
    organizationName: '민간단체2',
    createdAt: '2025.03.26 08:40',
    status: '모집',
    participants: 18,
    capacity: 20,
  },
  {
    id: 3,
    title: 'XX구 산불 피해 자원 봉사자 모집',
    organizationName: '민간단체3',
    createdAt: '2025.03.24 17:32',
    status: '완료',
    participants: 50,
    capacity: 50,
  },
];

interface DecodedToken {
  id: number;
  sub: string;
  iat: number;
  exp: number;
  userRole: 'IND' | 'NGO' | 'GOV';
}

const VolunteerPostListScreen = () => {
  const [isNGO, setIsNGO] = useState(false);

useEffect(() => {
  const checkToken = async () => {
    const token = await AsyncStorage.getItem('accessToken');
    console.log('🟢 가져온 accessToken:', token);

    if (!token) return;

    try {
      const decoded: DecodedToken = jwtDecode(token);
      console.log('✅ decoded:', decoded);
      setIsNGO(decoded.userRole === 'NGO');
    } catch (e) {
      console.error('❌ JWT decode error:', e);
    }
  };

  checkToken();
}, []);

  const navigation = useNavigation(); 

  return (
    <View style={styles.container}>
      {/* 상단 검색 바 */}
      <View style={styles.topBar}>
        <TextInput style={styles.searchInput} placeholder="파티 찾기 게시판" />
        <TouchableOpacity style={styles.searchButton}>
          <Text>검색</Text>
        </TouchableOpacity>
      </View>

      {/* 🟠 NGO 전용 버튼 */}
      <TouchableOpacity style={styles.createButton} onPress={() => navigation.navigate('PostCreate' as never)}>
        <Text style={styles.createButtonText}>+ 모집글 작성하기</Text>
      </TouchableOpacity>


      {/* 게시글 리스트 */}
      <FlatList
        data={mockPosts}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View style={styles.cardHeader}>
              <Text style={styles.status}>{item.status}</Text>
              <Text style={styles.count}>
                {item.participants} / {item.capacity}
              </Text>
            </View>
            <Text style={styles.title}>{item.title}</Text>
            <Text style={styles.subtext}>
              {item.organizationName}  {item.createdAt}
            </Text>
          </View>
        )}
      />
    </View>
  );
};

export default VolunteerPostListScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 16,
  },
  topBar: {
    flexDirection: 'row',
    marginBottom: 8,
  },
  searchInput: {
    flex: 1,
    borderColor: '#aaa',
    borderWidth: 1,
    padding: 8,
    borderRadius: 4,
  },
  searchButton: {
    marginLeft: 8,
    justifyContent: 'center',
    paddingHorizontal: 12,
    borderWidth: 1,
    borderRadius: 4,
    borderColor: '#ccc',
  },
  createButton: {
    alignSelf: 'flex-end',
    marginVertical: 8,
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 4,
    backgroundColor: '#f26522',
  },
  createButtonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  card: {
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 12,
    marginBottom: 8,
    borderRadius: 6,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  status: {
    color: '#f26522',
    fontWeight: 'bold',
  },
  count: {
    fontWeight: 'bold',
  },
  title: {
    marginTop: 6,
    fontSize: 16,
    fontWeight: '600',
  },
  subtext: {
    fontSize: 12,
    color: '#666',
    marginTop: 2,
  },
});
