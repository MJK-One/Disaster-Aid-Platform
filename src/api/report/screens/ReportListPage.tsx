import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  RefreshControl,
  Alert,
  ActivityIndicator,
} from 'react-native';
// import apiClient from '../api/axiosInstance';
import apiClient from '../../global/api/axiosInstance';

export default function ReportListPage() {
  const [reports, setReports] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [hasNext, setHasNext] = useState(false);
  const [page, setPage] = useState(0);

  const fetchReports = async (pageNum = 0, reset = true) => {
    try {
      if (reset) setLoading(true);

      // ▶ baseURL 에 이미 '/api' 가 들어가 있으므로, 여기서는 '/reports/my' 로만 호출
      const response = await apiClient.get('/reports/my', {
        params: { page: pageNum, size: 10 },
      });

      // ▶ Spring 에서 리턴하는 포맷은 { data: { payload: { content, ... } } }
      const payload = response.data.payload;
      const content: any[] = payload.content;
      const last: boolean = payload.last;

      if (reset) setReports(content);
      else setReports(prev => [...prev, ...content]);

      setHasNext(!last);
      setPage(pageNum);
    } catch (error: any) {
      console.error('[ReportList] Error:', 
        error.response?.status, 
        error.response?.data);
      Alert.alert('오류', '신고 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, []);

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchReports(0, true);
    setRefreshing(false);
  };

  const loadMore = () => {
    if (hasNext && !loading) {
      fetchReports(page + 1, false);
    }
  };

  const formatDate = (iso: string) => {
    const d = new Date(iso);
    return `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
  };

  if (loading && !refreshing) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#4ECDC4" />
        <Text style={styles.loadingText}>목록을 불러오는 중...</Text>
      </View>
    );
  }

    const renderHeader = () => (
    <Text style={styles.titleHeader}>신고목록</Text>
  );

  return (
    <View style={styles.container}>
       {/* ① 맨 위에 제목 */}
      {/* <Text style={styles.titleHeader}>신고목록</Text> */}
      <FlatList
        data={reports}
        keyExtractor={item => item.id.toString()}
        ListHeaderComponent={renderHeader}
        
        renderItem={({ item }) => (
          <View style={styles.card}>
            <Text style={styles.title}>{item.disasterType}</Text>
            <Text style={styles.status}>{item.status}</Text>
            <Text style={styles.date}>{formatDate(item.createdAt)}</Text>
            <Text style={styles.desc} numberOfLines={2}>
              {item.description}
            </Text>
          </View>
        )}
        onEndReached={loadMore}
        onEndReachedThreshold={0.5}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
        ListFooterComponent={hasNext ? <ActivityIndicator style={{ margin: 16 }} /> : null}
        ListEmptyComponent={() => (
          <View style={styles.empty}>
            <Text>신고 내역이 없습니다.</Text>
          </View>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
   // 제목 스타일
  titleHeader: {
    fontSize: 20,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 12,
  },

  card: { padding: 16, margin: 8, borderRadius: 8, backgroundColor: '#f1f1f1' },
  title: { fontSize: 16, fontWeight: 'bold' },
  status: { fontSize: 14, marginVertical: 4 },
  date: { fontSize: 12, color: '#666' },
  desc: { fontSize: 14, marginTop: 8 },
  loadingContainer: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  loadingText: { marginTop: 8, fontSize: 16 },
  empty: { flex: 1, justifyContent: 'center', alignItems: 'center' },
});