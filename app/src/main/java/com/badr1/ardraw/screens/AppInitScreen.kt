import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.badr1.ardraw.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.badr1.ardraw.ui.theme.PurpleBoxColor

// Note: Ensure InitViewModel and InitState are defined/imported correctly
// elsewhere in your project (as shown in previous responses).

@Composable
fun AppInitScreen(
    viewModel: InitViewModel,
    navigateToMain: () -> Unit // Function to navigate to your main activity/screen (with back stack clearing)
) {
    val state by viewModel.initState.collectAsState()

    when (state) {
        // --- Current State: Loading/Checking ---
        is InitState.Loading -> {
            LoadingSplashScreen()
        }

        // --- Final State: Success ---
        is InitState.Success -> {
            LaunchedEffect(Unit) {
                navigateToMain()
            }
        }

        // --- Final State: BLOCKED (First Run + No Internet) ---
        is InitState.WaitingForInternet -> {
            // Block the user and ask them to connect.
            InternetRequiredScreen(
                onRetry = {
                    viewModel.checkAndLoadData()
                }
            )
        }

        // --- Final State: Error ---
        is InitState.GenericError -> {
            ErrorScreen(
                message = "Failed to initialize data.",
                onRetry = { viewModel.checkAndLoadData() }
            )
        }
    }
}

@Composable
fun LoadingSplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Optional: Use a solid color background defined in your theme
            .background(color = PurpleBoxColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. App Logo/Icon
        Image(
            painter = painterResource(id = R.drawable.paint_palette), // Replace with your actual drawable ID
            contentDescription = "App Logo",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Progress Indicator (using the inverse color for visibility)
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 4.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Status Text
        Text(
            text = "Loading Data...",
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun InternetRequiredScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Icon (Visual representation of the problem)
        Icon(
            painter = painterResource(id = R.drawable.outline_signal_wifi_off_24),
            contentDescription = "No Internet Connection",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.error // Use the error color for prominence
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Clear Title (The Problem)
        Text(
            text = "Connection Required",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Informative Body (The Solution)
        Text(
            text = "Your first run requires essential data to be downloaded. Please check your network settings and try again.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 4. Action Button
        Button(
            onClick = onRetry,
            modifier = Modifier.height(56.dp)
        ) {
            Text(
                text = "Retry Connection",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Icon (Visual Warning)
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Generic Error",
            modifier = Modifier.size(96.dp),
            // Use the error color to signify a serious issue
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Clear Title
        Text(
            text = "Initialization Failed",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Informative Body (Shows the specific error message)
        Text(
            text = "A critical error occurred while loading application data. Details: $message",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 4. Action Button
        Button(
            onClick = onRetry,
            modifier = Modifier.height(56.dp)
        ) {
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}