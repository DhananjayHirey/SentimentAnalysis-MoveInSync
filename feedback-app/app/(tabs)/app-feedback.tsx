import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ScrollView,
} from "react-native";

export default function App() {
  const [config] = useState({
    enableDriverFeedback: true,
    enableTripFeedback: true,
    enableAppFeedback: true,
    enableMarshalFeedback: true,
  });

  const [formData, setFormData] = useState({
    entityType: "APP",
    entityId: "",
    rating: 5,
    comment: "",
  });

  const [status, setStatus] = useState("");

  const handleSubmit = async () => {
    if (!formData.entityId) {
      Alert.alert("Validation Error", "Please enter an ID");
      return;
    }

    setStatus("Submitting...");

    try {
      const response = await fetch("http://10.36.1.5:8080/api/feedback", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setStatus("Thank you for your feedback!");
        setFormData({ ...formData, entityId: "", comment: "" });
      } else {
        throw new Error("Submission failed");
      }
    } catch (error) {
      setStatus("Failed to submit feedback. Please try again.");
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>App Feedback</Text>

      <Text style={styles.label}>{formData.entityType} ID</Text>
      <TextInput
        style={styles.input}
        placeholder={`Enter ${formData.entityType} ID`}
        placeholderTextColor={"grey"}
        value={formData.entityId}
        onChangeText={(text) => setFormData({ ...formData, entityId: text })}
      />

      <Text style={styles.label}>Rating</Text>
      <View style={styles.ratingContainer}>
        {[1, 2, 3, 4, 5].map((star) => (
          <TouchableOpacity
            key={star}
            onPress={() => setFormData({ ...formData, rating: star })}
          >
            <Text
              style={[
                styles.star,
                star <= formData.rating && styles.selectedStar,
              ]}
            >
              ★
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={styles.label}>Comments</Text>
      <TextInput
        style={[styles.input, styles.textArea]}
        placeholder="Tell us more..."
        placeholderTextColor={"grey"}
        multiline
        value={formData.comment}
        onChangeText={(text) => setFormData({ ...formData, comment: text })}
      />

      <TouchableOpacity style={styles.submitButton} onPress={handleSubmit}>
        <Text style={styles.submitText}>Submit Feedback</Text>
      </TouchableOpacity>

      {status !== "" && <Text style={styles.status}>{status}</Text>}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
    paddingTop: 60,
    backgroundColor: "#111",
    flexGrow: 1,
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    color: "white",
    textAlign: "center",
    marginBottom: 20,
  },
  label: {
    color: "#ccc",
    marginBottom: 5,
    marginTop: 15,
  },
  grid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10,
  },
  typeButton: {
    flex: 1,
    padding: 10,
    borderWidth: 1,
    borderColor: "#333",
    borderRadius: 8,
    margin: 5,
    alignItems: "center",
  },
  selectedButton: {
    backgroundColor: "#007bff",
    borderColor: "#007bff",
  },
  typeButtonText: {
    color: "#ccc",
  },
  selectedText: {
    color: "white",
  },
  input: {
    backgroundColor: "#222",
    color: "white",
    padding: 12,
    borderRadius: 8,
  },
  textArea: {
    height: 100,
    textAlignVertical: "top",
  },
  ratingContainer: {
    flexDirection: "row",
    justifyContent: "center",
    marginVertical: 10,
  },
  star: {
    fontSize: 32,
    color: "#555",
    marginHorizontal: 5,
  },
  selectedStar: {
    color: "#FFD700",
  },
  submitButton: {
    backgroundColor: "#28a745",
    padding: 15,
    borderRadius: 8,
    marginTop: 20,
    alignItems: "center",
  },
  submitText: {
    color: "white",
    fontWeight: "bold",
  },
  status: {
    marginTop: 15,
    textAlign: "center",
    color: "#ccc",
  },
});
