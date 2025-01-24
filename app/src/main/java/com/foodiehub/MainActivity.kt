package com.foodiehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.foodiehub.ui.theme.FoodieHubTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodieHubTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel = viewModel<UserViewModel>()

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") { OnboardingScreens(navController) }
        composable("signUp") { SignUpScreen(viewModel = userViewModel, navController = navController) }
        composable("signIn") { SignInScreen(viewModel = userViewModel, navController = navController) }
        composable("recipeApp") {
            RecipeApp(viewModel = RecipeViewModel(LocalContext.current), navController = navController , userViewModel = userViewModel)
        }
        composable("favorites_screen") {
            FavoritesScreen(viewModel = RecipeViewModel(LocalContext.current), navController = navController)
        }
        composable(
            route = "recipeDetail/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId")
            if (recipeId != null) {
                RecipeDetailScreen(recipeId = recipeId, viewModel = RecipeViewModel(LocalContext.current) , navController = navController)
            }
        }
    }
}

@Composable
fun FavoritesScreen(viewModel: RecipeViewModel , navController: NavController) {
    val favorites = viewModel.favorites

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(favorites) { recipe ->
                RecipeCard(recipe = recipe, navController = navController) // Show favorite recipes here
            }
        }
    }
}

@Composable
fun SignUpScreen(viewModel: UserViewModel, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Regex Patterns for validation
    val usernameRegex = "^[a-zA-Z0-9]{4,}$".toRegex()
    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
    val passwordRegex =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\\\$!%*?&])[A-Za-z\\d@\\\$!%*?&]{8,}$".toRegex()

    fun validateInput(): Boolean {
        var isValid = true

        // Validate Username
        if (!usernameRegex.matches(username)) {
            usernameError = "Username must be alphanumeric and at least 4 characters"
            isValid = false
        } else {
            usernameError = ""
        }

        // Validate Email
        if (!emailRegex.matches(email)) {
            emailError = "Invalid email format"
            isValid = false
        } else {
            emailError = ""
        }

        // Validate Password
        if (!passwordRegex.matches(password)) {
            passwordError =
                "Password must be at least 8 characters, with uppercase, lowercase, number, and special character."
            isValid = false
        } else {
            passwordError = ""
        }

        return isValid
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // Automatically adds padding for system bars (top, bottom)
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Create your account",
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your details to register",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Username Field
            Text(
                text = "Username",
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Eg. Umar Maaz",
                    color = Color.Gray,) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon",
                    tint = Color.DarkGray) },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            if (usernameError.isNotEmpty()) {
                Text(
                    text = usernameError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Email Field
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Email",
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Eg. maaz12@gmail.com",
                    color = Color.Gray,) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon",
                    tint = Color.DarkGray) },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Password Field
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Password",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

// State to toggle password visibility
            var isPasswordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Eg. Password123!",
                    color = Color.Gray,) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon",
                    tint = Color.DarkGray) },
                trailingIcon = {
                    if (password.isNotEmpty()) { // Show the eye icon only if the password is not empty
                        val icon =
                            if (isPasswordVisible) painterResource(id = R.drawable.eye_open) else painterResource(
                                id = R.drawable.eye_close
                            )
                        val description =
                            if (isPasswordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                icon,
                                contentDescription = description,
                                tint = Color.DarkGray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }


            // Sign Up Button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (validateInput()) {
                        viewModel.signUp(username, email, password)
                        navController.navigate("signIn") // Navigate to sign-in page after sign-up
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0C9A62),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "Sign Up",
                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                )
            }

            // "Or sign up with" text and icons
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), // Ensures the row takes enough height for dividers
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically // Ensures vertical centering of text and dividers
            ) {
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                ) // Adjust divider height if needed
                Text(
                    text = " Or sign up with ",
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                ) // Adjust divider height if needed
            }

            // Google and Facebook icons
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { /* Handle Google sign up */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { /* Handle Facebook sign up */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Facebook Icon",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            // Already have an account section
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { navController.navigate("signIn") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(style = SpanStyle(color = Color(0xFF0C9A62))) {
                            append("Sign In")
                        }
                    },
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray // Default color for "Already have an account?"
                )
            }
        }
    }
}

@Composable
fun SignInScreen(viewModel: UserViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp)
                .background(Color.White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Welcome Back Header
            Text(
                text = "Welcome Back",
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your credentials to sign in",
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email Input Field
            Text(
                text = "Email",
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email",
                    color = Color.Gray,) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon",
                    tint = Color.DarkGray) },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Password Input Field
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Password",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

// State to manage password visibility
            var isPasswordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter your password",
                    color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon",
                    tint = Color.DarkGray) },
                trailingIcon = {
                    if (password.isNotEmpty()) { // Show the eye icon only if password is not empty
                        val icon =
                            if (isPasswordVisible) painterResource(id = R.drawable.eye_open) else painterResource(
                                id = R.drawable.eye_close
                            )
                        val description =
                            if (isPasswordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = icon,
                                contentDescription = description,
                                tint = Color.DarkGray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray
                )
            )

            // Error Message
            Spacer(modifier = Modifier.height(16.dp))
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }


            // Sign In Button
            Button(
                onClick = {
                    viewModel.signIn(email, password)
                    val user = viewModel.currentUser.value
                    if (user != null) {
                        navController.navigate("recipeApp") // Navigate to recipe app after successful sign-in
                    } else {
                        errorMessage = "Incorrect email or password. Please try again."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0C9A62),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Sign In", fontFamily = FontFamily(Font(R.font.poppins_regular)))
            }

            // Or sign in with section
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Or sign in with",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            // Icons Row (Google and Facebook)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { /* Handle Google Sign In */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { /* Handle Facebook Sign In */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Facebook Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

// Sign Up Button
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate("signUp") }) {
                Text(
                    text = buildAnnotatedString {
                        append("Don't have an account? ")
                        withStyle(style = SpanStyle(color = Color(0xFF0C9A62))) { // Green color for "Sign Up"
                            append("Sign Up")
                        }
                    },
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    textAlign = TextAlign.Center,
                    color = Color.Gray, // Default color for the rest of the text
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun RecipeApp(viewModel: RecipeViewModel, navController: NavController, userViewModel: UserViewModel) {
    val categories = viewModel.categories.value
    val selectedCategory = viewModel.selectedCategory.value
    val filteredRecipes = viewModel.filteredRecipes.value
    val user = userViewModel.currentUser.value
    val isChatOpen = remember { mutableStateOf(false) } // State to toggle chatbot UI
    val chatMessages =
        remember { mutableStateListOf<Pair<String, Boolean>>() } // List of chat messages

    // Main Column
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(WindowInsets.systemBars.asPaddingValues())
        .background(Color.White)) {
        Column(
        ) {
            // Username and Favorites
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            ) {
                Text(
                    text = "Hi, ${user?.username ?: "User"}",
                    fontFamily = FontFamily(Font(R.font.poppins_bold)),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.hello),
                    contentDescription = "User Icon",
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { navController.navigate("favorites_screen") },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorites",
                        tint = Color(0xFF0C9A62)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Got a tasty dish in mind?",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            SearchBar(
                query = viewModel.searchQuery.value,
                onQueryChange = { viewModel.setSearchQuery(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CategoryButtons(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setSelectedCategory(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRecipes) { recipe ->
                    RecipeCard(recipe = recipe, navController = navController)
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { isChatOpen.value = true },
            containerColor = Color(0xFF0C9A62),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.chat_bot),
                contentDescription = "Chat",
                modifier = Modifier.size(24.dp)
            )
        }

        // Chatbot Dialog
        if (isChatOpen.value) {
            ChatBotDialog(
                chatMessages = chatMessages,
                onDismiss = { isChatOpen.value = false },
                onSendMessage = { message ->
                    chatMessages.add(Pair(message, true)) // User message
                    // Add chatbot response
                    chatMessages.add(Pair(predefinedChatResponse(message), false))
                }
            )
        }
    }
}

@Composable
fun ChatBotDialog(
    chatMessages: List<Pair<String, Boolean>>,
    onDismiss: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // Header with Back Button and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0C9A62)
                    )
                }
                Text(
                    text = "Recipe Assistant",
                    fontFamily = FontFamily(Font(R.font.poppins_bold)),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Chat Messages
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(chatMessages) { (message, isUser) ->
                    Text(
                        text = message,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        color = if (isUser) Color.Blue else Color.Black,
                        modifier = Modifier
                            .align(if (isUser) Alignment.End else Alignment.Start)
                            .padding(4.dp)
                            .background(
                                if (isUser) Color(0xFFE3F2FD) else Color(0xFFF1F8E9),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    )
                }
            }

            // Input Section
            Row {
                var userInput by remember { mutableStateOf("") }
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text("Ask me something...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            onSendMessage(userInput)
                            userInput = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color(0xFF0C9A62)
                    )
                }
            }
        }
    }
}

fun predefinedChatResponse(message: String): String {
    val lowerMessage = message.lowercase()
    return when {
        "recipe" in lowerMessage -> "Looking for a recipe? Try our most popular dish: Spaghetti Carbonara!"
        "category" in lowerMessage -> "We have categories like Desserts, Quick Meals, and Vegan. Which one interests you?"
        "tip" in lowerMessage -> "Here's a tip: Always let your meat rest for a few minutes after cooking!"
        "popular" in lowerMessage -> "Our most popular recipes include Chicken Alfredo, Tiramisu, and Vegan Buddha Bowls!"
        "ingredient" in lowerMessage -> "Looking for recipes with a specific ingredient? Let me know the ingredient!"
        else -> "I'm here to help! You can ask about recipes, categories, cooking tips, or popular dishes!"
    }
}

@Composable
fun OnboardingScreens(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            title = "Get all the recipes that you need now",
            description = "Whether you are losing or gaining, we have all the recipes you’ll need.",
            imageRes = R.drawable.onboard_1,
            backgroundColor = Color(0xFFE8F5E9)
        ),
        OnboardingPage(
            title = "Uncover a world of recipes for every occasion",
            description = "We are updating our food database every minute to help you.",
            imageRes = R.drawable.onboard_2,
            backgroundColor = Color(0xFFFFF3E9)
        ),
        OnboardingPage(
            title = "Every recipe you need, just a tap away.",
            description = "We are updating our food database every minute to help you.",
            imageRes = R.drawable.onboard3,
            backgroundColor = Color(0xFFE3F2FD)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(

        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .background(Color.White)
    ) {
        // Top layout for image (60% height)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f) // 60% of the screen height
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(pages[page].backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = pages[page].imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Bottom layout (40% height)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f) // 40% of the screen height
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp) // Slight overlap with top layout
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Dots Indicator
                DotsIndicator(
                    totalDots = pages.size,
                    selectedIndex = pagerState.currentPage,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = pages[pagerState.currentPage].title,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = pages[pagerState.currentPage].description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                navController.navigate("signUp") // Navigate to SignUpScreen instead of recipeApp
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C9A62))
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(recipeId: Int, viewModel: RecipeViewModel , navController: NavController) {
    val recipe = viewModel.getRecipeById(recipeId)
    var selectedTab by remember { mutableStateOf("Ingredients") } // To manage tab selection
    var isHeartFilled by remember { mutableStateOf(false) } // State for heart icon fill toggle

    if (recipe != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .systemBarsPadding() // Automatically adds padding for system bars (top, bottom)
        ) {
            // Box to overlay Back Button and Heart Icon on Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Adjust as per your requirement
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            ) {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Back Button - top left
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                // Heart Icon - top right
                IconButton(
                    onClick = {
                        viewModel.toggleFavorite(recipe) // Toggle favorite
                        isHeartFilled = !isHeartFilled
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isHeartFilled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isHeartFilled) Color(0xFF0C9A62) else Color.White
                    )
                }
            }

            // Bottom Info Layout
            Box(
                modifier = Modifier
                    .weight(1f) // Takes 50% height
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Recipe Title and Time
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f),
                            fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.timer),
                                contentDescription = "Time",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = recipe.preparation_time,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.poppins_regular))
                            )
                        }
                    }

                    // Recipe Description
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Tab Row for Ingredients and Instructions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TabButton(
                            text = "Ingredients",
                            isSelected = selectedTab == "Ingredients",
                            onClick = { selectedTab = "Ingredients" }
                        )
                        TabButton(
                            text = "Instruction",
                            isSelected = selectedTab == "Instruction",
                            onClick = { selectedTab = "Instruction" }
                        )
                    }

                    // Content based on selected tab
                    when (selectedTab) {
                        "Ingredients" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Ingredients (${recipe.ingredients.size} items)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                    modifier = Modifier.padding(bottom = 8.dp),
                                )
                                recipe.ingredients.forEach { ingredient ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Ingredient",
                                            tint = Color(0xFF0C9A62)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${ingredient.name}: ${ingredient.quantity}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }
                            }
                        }
                        "Instruction" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Instructions (${recipe.instructions.size} steps)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                recipe.instructions.forEachIndexed { index, step ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        // Bullet point icon
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Bullet",
                                            tint = Color(0xFF0C9A62),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "$step",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Recipe not found.", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF0C9A62) else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .height(40.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily(Font(R.font.poppins_regular)))
    }
}

@Composable
fun RecipeCard(recipe: Recipe, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(220.dp)
            .clickable {
                navController.navigate("recipeDetail/${recipe.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Image Section
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            // Text Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Recipe Name
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Preparation Time with Clock Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.timer),
                        contentDescription = "Preparation Time",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Space between icon and text
                    Text(
                        text = recipe.preparation_time,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Ratings
                Text(
                    text = "★★★★☆ (4.0)", // Dummy rating
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFA000) // Orange color for rating
                )
            }
        }
    }
}

// Function to get the icon for each category dynamically (you can adjust it based on your category data)
fun getCategoryIcon(category: RecipeCategory): Int {
    return when (category.name) {
        "Breakfast" -> R.drawable.breakfast_img // Replace with your actual resource ID
        "Main Course" -> R.drawable.main_course_img
        "Desserts" -> R.drawable.desserts_img
        "Beverages" -> R.drawable.beverages_img
        "Snacks" -> R.drawable.snacks_img
        "Seafood" -> R.drawable.seafood_img
        else -> R.drawable.all_img
    }
}

@Composable
fun DotsIndicator(totalDots: Int, selectedIndex: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .height(7.dp)
                    .width(if (index == selectedIndex) 35.dp else 25.dp) // Wider selected dot
                    .padding(horizontal = 4.dp)
                    .background(
                        color = if (index == selectedIndex) Color(0xFF0C9A62) else Color.LightGray,
                        shape = RoundedCornerShape(50) // Rounded corners for wider dots
                    )
            )
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(text = "Search Recipes"
        , style = MaterialTheme.typography.bodyMedium,
            ) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search, // Using the search icon from the Icons.Default
                contentDescription = "Search Icon"
            )
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, // Hide the indicator line when focused
            unfocusedIndicatorColor = Color.Transparent, // Hide the indicator line when not focused
            focusedContainerColor = Color.LightGray,
            unfocusedContainerColor = Color.LightGray
        )
    )
}

@Composable
fun CategoryButton(
    text: String,
    isSelected: Boolean,
    icon: Painter,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp) // Padding for the whole column
    ) {
        // Category Button with Icon
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp), // Rounded corners for the button
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) Color(0xFF0C9A62) else Color.White, // Primary color for selected, white for default
                contentColor = if (isSelected) Color.White else Color.Black // Text color for selected and default
            ),
            contentPadding = PaddingValues(8.dp), // Padding inside button
            modifier = Modifier
                .size(60.dp) // Fixed size for the button
        ) {
            // Category Icon (Image)
            Image(
                painter = icon,
                contentDescription = text,
                modifier = Modifier
                    .size(36.dp) // Adjust the icon size
            )
        }

        // Category Text (Placed outside the button)
        Text(
            text = text,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.Black else Color.Black, // Color for text based on selection
            modifier = Modifier.padding(top = 4.dp) // Padding between button and text
        )
    }
}

@Composable
fun CategoryButtons(
    categories: List<RecipeCategory>,
    selectedCategory: RecipeCategory?,
    onCategorySelected: (RecipeCategory?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp) // Adds spacing between buttons
    ) {
        // "All Categories" button
        item {
            CategoryButton(
                text = "All",
                isSelected = selectedCategory == null,
                icon = painterResource(id = R.drawable.all_img), // Replace with your actual icon
                onClick = { onCategorySelected(null) }
            )
        }

        // Category buttons with their respective icons
        items(categories) { category ->
            CategoryButton(
                text = category.name,
                isSelected = category == selectedCategory,
                icon = painterResource(id = getCategoryIcon(category)), // Get the icon for each category dynamically
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int,
    val backgroundColor: Color
)

data class RecipeCategory(
    val id: Int,
    val name: String,
    val filters: Filters,
    val recipes: List<Recipe>
)

data class Filters(
    val preparation_time: List<String>,
    val difficulty: List<String>,
    val dietary: List<String>,
    val allergens: List<String>
)

data class Recipe(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val nutritional_info:NutritionalInfo, // Change to an object,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val preparation_time: String,
    val difficulty: String,
    val tags: List<String>
)

data class NutritionalInfo( // Representing the object structure
    val calories: String,
    val fat: String,
    val carbohydrates: String,
    val protein: String,
    val fiber: String
)

data class Ingredient(
    val name: String,
    val quantity: String,
    val description: String
)

