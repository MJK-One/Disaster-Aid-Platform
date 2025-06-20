import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { Text } from 'react-native';
import Layout from './src/components/Layout';
import { SafeAreaProvider } from 'react-native-safe-area-context';

const App = () => {
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <Layout>
          <Text>본문</Text>
        </Layout>
      </NavigationContainer>
    </SafeAreaProvider>
  );
};

export default App;