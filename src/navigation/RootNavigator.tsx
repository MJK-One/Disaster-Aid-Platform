import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../api/auth/screens/loginScreen';
import SignUpScreen from '../api/auth/screens/singupScreen';
import WelcomeScreen from '../WelcomeScreen';

import VolunteerPostListScreen from '../api/volunteer/screens/VolunteerPostListScreen';
import VolunteerPostCreateScreen from '../api/volunteer/screens/VolunteerPostCreateScreen';
import VolunteerPostDetailScreen from '../api/volunteer/screens/VolunteerPostDetailScreen';

import MyActivitiesScreen from '../api/report/screens/MyActivitiesScreen';

import ReportListPage from '../api/report/screens/ReportListPage'
import AlertScreen from '../api/alert/screens/AlertScreen';

import Layout from '../components/Layout';
import ReportScreen from '../api/report/screens/ReportScreen';
import DashboardScreen from '../api/report/screens/DashboardScreen';

export type RootStackParamList = {
  Welcome: undefined;
  Login: undefined;
  SignUp: undefined;
  VolunteerPosts: undefined;
  PostCreate: undefined;
  PostDetail: { postId: number };
  MyActivities: undefined;
  ReportList: undefined;
  ReportScreen: undefined;
  Dashboard: undefined;
  Alert: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

const withLayout = (Component: React.ComponentType<any>) => {
  return (props: any) => (
    <Layout>
      <Component {...props} />
    </Layout>
  );
};

const RootNavigator = () => {
  return (
    <Stack.Navigator initialRouteName="Login">
      {/* ❌ Layout 미적용 */}
      <Stack.Screen name="Login" component={LoginScreen} options={{ headerShown: false }} />
      <Stack.Screen name="SignUp" component={SignUpScreen} options={{ headerShown: false }} />

      {/* ✅ Layout 적용 */}
      <Stack.Screen name="Welcome" component={withLayout(WelcomeScreen)} options={{ headerShown: false }} />
      <Stack.Screen name="VolunteerPosts" component={withLayout(VolunteerPostListScreen)} options={{ headerShown: false }} />
      <Stack.Screen name="PostCreate" component={withLayout(VolunteerPostCreateScreen)} options={{ headerShown: false }} />
      <Stack.Screen name="PostDetail" component={withLayout(VolunteerPostDetailScreen)} options={{ headerShown: true }} />
      <Stack.Screen name="MyActivities" component={withLayout(MyActivitiesScreen)} options={{ title: '나의 활동' }} />
      <Stack.Screen name="ReportList" component={withLayout(ReportListPage)} options={{ title: '내 신고 목록' }} />
      <Stack.Screen name="ReportScreen" component={withLayout(ReportScreen)} options={{ title: '신고 할께요' }} />
      <Stack.Screen name="Dashboard" component={withLayout(DashboardScreen)} options={{ title: '대쉬보드'}} />
      <Stack.Screen name="Alert" component={withLayout(AlertScreen)} options={{ title: '내 알림'}} /> 
    </Stack.Navigator>
  );
};

export default RootNavigator;