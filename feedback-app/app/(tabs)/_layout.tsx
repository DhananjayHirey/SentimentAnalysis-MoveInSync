import { Tabs } from "expo-router";
import React from "react";

import { HapticTab } from "@/components/haptic-tab";
import { IconSymbol } from "@/components/ui/icon-symbol";
import { Colors } from "@/constants/theme";
import { useColorScheme } from "@/hooks/use-color-scheme";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";

export default function TabLayout() {
  const colorScheme = useColorScheme();

  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: Colors[colorScheme ?? "light"].tint,
        headerShown: false,
        tabBarButton: HapticTab,
      }}
    >
      <Tabs.Screen
        name="driver-feedback"
        options={{
          title: "Driver",
          tabBarIcon: ({ color }) => (
            <MaterialIcons size={28} color={color} name="man" />
          ),
        }}
      />
      <Tabs.Screen
        name="trip-feedback"
        options={{
          title: "Trip",
          tabBarIcon: ({ color }) => (
            <MaterialIcons size={28} color={color} name="map" />
          ),
        }}
      />
      <Tabs.Screen
        name="marshal-feedback"
        options={{
          title: "Marshal",
          tabBarIcon: ({ color }) => (
            <MaterialIcons size={28} color={color} name="add-moderator" />
          ),
        }}
      />
      <Tabs.Screen
        name="app-feedback"
        options={{
          title: "App",
          tabBarIcon: ({ color }) => (
            <MaterialIcons size={28} color={color} name="app-shortcut" />
          ),
        }}
      />
    </Tabs>
  );
}
