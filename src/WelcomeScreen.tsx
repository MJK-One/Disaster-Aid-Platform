import React from 'react';
import { TouchableOpacity, Text, View } from 'react-native';
import { useNavigation } from '@react-navigation/native';

const WelcomeScreen = () => {
  const navigation = useNavigation();

  return (
    <View>
      <TouchableOpacity onPress={() => navigation.navigate('ReportScreen' as never)}>
        <Text>신고</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate('Dashboard' as never)}>
        <Text>대쉬보드</Text>
      </TouchableOpacity>
    </View>
  );
};

export default WelcomeScreen;
