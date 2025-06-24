import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../api/auth/screens/loginScreen';
import SignUpScreen from '../api/auth/screens/singupScreen';
import WelcomeScreen from '../WelcomeScreen';
// import VolunteerPostListScreen from '../api/volunteer/screens/VolunteerPostListScreen';
// import VolunteerPostCreateScreen from '../api/volunteer/screens/VolunteerPostCreateScreen';
import MyActivitiesScreen from '../api/report/screens/MyActivitiesScreen';
import ReportListPage from '../api/report/screens/ReportListPage'
import AlertScreen from '../api/alert/screens/AlertScreen';

const Stack = createNativeStackNavigator();

const RootNavigator = () => {
  return (
    <Stack.Navigator initialRouteName="Welcome">
      <Stack.Screen name="Welcome" component={WelcomeScreen} options={{ headerShown: false }} />
      <Stack.Screen name="Login" component={LoginScreen} options={{ headerShown: false }} />
      <Stack.Screen name="SignUp" component={SignUpScreen} options={{ headerShown: false }} />
      <Stack.Screen name="MyActivities" component={MyActivitiesScreen}options={{ title: '나의 활동' }} />
      <Stack.Screen name="ReportList" component={ReportListPage} options={{ title: '내 신고 목록' }} />
      <Stack.Screen name="Alert" component={AlertScreen} options={{ headerShown: false }} /> 

    {/* <Stack.Screen name="Attendance" component={AttendanceScreen} options={{ title: '출석' }} />
    <Stack.Screen name="Participants" component={ParticipantsScreen} options={{ title: '참가자' }}/> */}
    </Stack.Navigator>
  );
};

export default RootNavigator;