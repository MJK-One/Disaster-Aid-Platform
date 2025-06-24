import React, { useEffect } from 'react';
import { Alert } from 'react-native';
import messaging from '@react-native-firebase/messaging';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import RootNavigator from './src/navigation/RootNavigator';
import { requestPushPermission } from './src/api/alert/fcm/fcmPermissions';
import { getFcmToken } from './src/api/alert/fcm/fcmTokenManager';

const App = () => {
  useEffect(() => {
    const initFCM = async () => {
      const granted = await requestPushPermission();
      if (!granted) {
        console.warn('❌ FCM 권한 거부됨');
        return;
      }

      const token = await getFcmToken();
      if (token) console.log('📱 FCM 토큰:', token);
    };

    const unsubscribe = messaging().onMessage(async remoteMessage => {
      Alert.alert('📩 새 알림', remoteMessage.notification?.body || '');
    });

    initFCM();

    return () => {
      unsubscribe();
    };
  }, []);

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    </SafeAreaProvider>
  );
};

export default App;
