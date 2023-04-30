package com.sanmer.mrepo.ui.screens.modules

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.activity.install.InstallActivity
import com.sanmer.mrepo.ui.navigation.navigateToHome
import com.sanmer.mrepo.ui.screens.modules.pages.CloudPage
import com.sanmer.mrepo.ui.screens.modules.pages.InstalledPage
import com.sanmer.mrepo.ui.screens.modules.pages.UpdatablePage
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun ModulesScreen(
    navController: NavController,
    viewModel: ModulesViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pagerState = rememberPagerState(initialPage = Pages.Cloud.id)

    BackHandler {
        if (viewModel.isSearch) {
            viewModel.closeSearch()
        } else {
            navController.navigateToHome()
        }
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.closeSearch() }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ModulesTopBar(
                userData = userData,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (userData.isRoot && !viewModel.isSearch) {
                InstallFloatingButton()
            }
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TabsItem(
                state = pagerState,
                userData = userData,
                scrollBehavior = scrollBehavior
            )

            HorizontalPager(
                pageCount = pages.size,
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    pagerSnapDistance = PagerSnapDistance.atMost(0)
                ),
                userScrollEnabled = if (viewModel.isSearch) false else userData.isRoot
            ) {
                when (it) {
                    Pages.Cloud.id -> CloudPage(
                        userData = userData,
                        navController = navController
                    )

                    Pages.Installed.id -> InstalledPage()

                    Pages.Updatable.id -> UpdatablePage(
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun ModulesTopBar(
    userData: UserData,
    viewModel: ModulesViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior,
) = if (viewModel.isSearch) {
    ModulesSearchTopBar(
        scrollBehavior = scrollBehavior
    )
} else {
    ModulesNormalTopBar(
        userData = userData,
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun ModulesNormalTopBar(
    userData: UserData,
    viewModel: ModulesViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(text = stringResource(id = R.string.page_modules))
    },
    actions = {
        IconButton(
            onClick = { viewModel.isSearch = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_normal_outline),
                contentDescription = null
            )
        }

        var expanded by remember { mutableStateOf(false) }
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )

            MenuItem(
                userData = userData,
                expanded = expanded,
                onClose = { expanded = false }
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun ModulesSearchTopBar(
    viewModel: ModulesViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    navigationIcon = {
        IconButton(
            onClick = {
                viewModel.closeSearch()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close_square_outline),
                contentDescription = null
            )
        }
    },
    title = {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequester),
            value = viewModel.key,
            onValueChange = { viewModel.key = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions {
                defaultKeyboardAction(ImeAction.Search)
            },
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search_normal_outline),
                    contentDescription = null
                )
            },
            placeholder = {
                Text(text = stringResource(id = R.string.modules_page_search_placeholder))
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun InstallFloatingButton() {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        InstallActivity.start(context = context, uri = uri)
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                launcher.launch("application/zip")
            }
        }
    }

    ExtendedFloatingActionButton(
        interactionSource = interactionSource,
        onClick = {},
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary,
        icon = {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = R.drawable.add_outline),
                contentDescription = null
            )
        },
        text = {
            Text(text = stringResource(id = R.string.module_install))
        }
    )
}