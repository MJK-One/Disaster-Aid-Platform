import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { jwtDecode } from 'jwt-decode';
import { useNavigation } from '@react-navigation/native';
import { volunteerpostsApi } from '../api/VolunteerApi';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import type { RootStackParamList } from '../../../navigation/RootNavigator';

interface VolunteerPost {
  id: number;
  title: string;
  createdAt: string;
  category: string;
  capacity: number;
  nickname: string;
}

interface DecodedToken {
  id: number;
  sub: string;
  iat: number;
  exp: number;
  userRole: 'IND' | 'NGO' | 'GOV';
}

const VolunteerPostListScreen = () => {
  const [isNGO, setIsNGO] = useState(false);
  const [posts, setPosts] = useState<VolunteerPost[]>([]);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);

  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();

  useEffect(() => {
    const checkToken = async () => {
      const token = await AsyncStorage.getItem('accessToken');
      if (!token) return;

      try {
        const decoded: DecodedToken = jwtDecode(token);
        setIsNGO(decoded.userRole === 'NGO');
      } catch (e) {
        console.error('JWT decode error:', e);
      }
    };
    checkToken();
    fetchPosts(0);
  }, []);

  const fetchPosts = async (pageNumber: number) => {
    if (loading || !hasMore) return;
    setLoading(true);
    try {
      const data = await volunteerpostsApi.fetchPosts(pageNumber);
      const newPosts = data.content;
      setPosts((prev) => [...prev, ...newPosts]);
      setHasMore(!data.last);
      setPage(pageNumber + 1);
    } catch (e) {
      console.error('Failed to load posts:', e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.topBar}>
        <TextInput style={styles.searchInput} placeholder="파티 찾기 게시판" />
        <TouchableOpacity style={styles.searchButton}>
          <Text>검색</Text>
        </TouchableOpacity>
      </View>

      {isNGO && (
        <TouchableOpacity style={styles.createButton} onPress={() => navigation.navigate('PostCreate')}>
          <Text style={styles.createButtonText}>+ 모집글 작성하기</Text>
        </TouchableOpacity>
      )}

      <FlatList
        data={posts}
        keyExtractor={(item) => item.id.toString()}
        onEndReached={() => fetchPosts(page)}
        onEndReachedThreshold={0.5}
        ListFooterComponent={loading ? <ActivityIndicator /> : null}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => navigation.navigate('PostDetail', { postId: item.id })}>
            <View style={styles.card}>
              <Text style={styles.title}>{item.title}</Text>
              <Text style={styles.subtext}>{item.nickname} | {item.createdAt}</Text>
              <Text style={styles.capacity}>모집인원: {item.capacity}</Text>
            </View>
          </TouchableOpacity>
        )}
      />
    </View>
  );
};

export default VolunteerPostListScreen;

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff', padding: 16 },
  topBar: { flexDirection: 'row', marginBottom: 8 },
  searchInput: { flex: 1, borderColor: '#aaa', borderWidth: 1, padding: 8, borderRadius: 4 },
  searchButton: { marginLeft: 8, justifyContent: 'center', paddingHorizontal: 12, borderWidth: 1, borderRadius: 4, borderColor: '#ccc' },
  createButton: { alignSelf: 'flex-end', marginVertical: 8, paddingHorizontal: 14, paddingVertical: 6, borderRadius: 4, backgroundColor: '#f26522' },
  createButtonText: { color: '#fff', fontWeight: 'bold' },
  card: { borderWidth: 1, borderColor: '#ddd', padding: 12, marginBottom: 8, borderRadius: 6 },
  title: { fontSize: 16, fontWeight: '600', marginBottom: 4 },
  subtext: { fontSize: 12, color: '#666' },
  capacity: { fontSize: 12, marginTop: 4 },
});
