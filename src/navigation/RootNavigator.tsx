import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../api/auth/screens/loginScreen';
import SignUpScreen from '../api/auth/screens/singupScreen';
import WelcomeScreen from '../WelcomeScreen';
import VolunteerPostListScreen from '../api/volunteer/screens/VolunteerPostListScreen';
import VolunteerPostCreateScreen from '../api/volunteer/screens/VolunteerPostCreateScreen';
import Layout from '../components/Layout';

const Stack = createNativeStackNavigator();

const withLayout = (Component: React.ComponentType) => {
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
    </Stack.Navigator>
  );
};

export default RootNavigator;