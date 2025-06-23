import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import Layout from './src/components/Layout';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import RootNavigator from './src/navigation/RootNavigator';

const App = () => {
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <Layout>
          <RootNavigator />
        </Layout>
      </NavigationContainer>
    </SafeAreaProvider>
  );
};

export default App;