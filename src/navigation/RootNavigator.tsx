import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../api/auth/screens/loginScreen';
import SignUpScreen from '../api/auth/screens/singupScreen';
import WelcomeScreen from '../WelcomeScreen';
import VolunteerPostListScreen from '../api/volunteer/screens/VolunteerPostListScreen';
import VolunteerPostCreateScreen from '../api/volunteer/screens/VolunteerPostCreateScreen';

const Stack = createNativeStackNavigator();

const RootNavigator = () => {
  return (
    <Stack.Navigator initialRouteName="Welcome">
      <Stack.Screen name="Welcome" component={WelcomeScreen} options={{ headerShown: false }} />
      <Stack.Screen name="Login" component={LoginScreen} options={{ headerShown: false }} />
      <Stack.Screen name="SignUp" component={SignUpScreen} options={{ headerShown: false }} />
      <Stack.Screen name="VolunteerPosts" component={VolunteerPostListScreen} options={{ headerShown: false }}/>
      <Stack.Screen name="PostCreate" component={VolunteerPostCreateScreen} options={{ headerShown: false }}/>
    </Stack.Navigator>
  );
};

export default RootNavigator;