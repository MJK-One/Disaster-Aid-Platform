import React from 'react';
import {
  Text,
  StyleSheet,
} from 'react-native';

const WelcomeScreen = () => {
  return (
      <Text style={styles.title}>삐뽀B4</Text>
  );
};

const styles = StyleSheet.create({
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    marginBottom: 40,
    color: '#333',
  },
});

export default WelcomeScreen;