package com.example.familypizza.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.familypizza.R
import com.example.familypizza.presentation.theme.*

@Composable
fun LogoHeader(title: String, subtitle: String) {
    Image(
        painter = painterResource(R.drawable.logosinfondo),
        contentDescription = "FamilyPizza",
        modifier = Modifier.size(120.dp).clip(CircleShape),
        contentScale = ContentScale.Fit
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
    Text(subtitle, color = FamilyMuted, style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
fun FamilyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun PrimaryAction(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FamilyRed),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(text, fontWeight = FontWeight.Black)
    }
}

@Composable
fun AuthLink(text: String, onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        color = FamilyRed,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
fun ProfileInfo(label: String, value: String) {
    Surface(
        color = androidx.compose.ui.graphics.Color.White,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, color = FamilyMuted, style = MaterialTheme.typography.labelMedium)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}