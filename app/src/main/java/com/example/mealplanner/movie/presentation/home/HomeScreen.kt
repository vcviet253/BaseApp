@file:OptIn(ExperimentalFoundationApi::class)

package com.example.mealplanner.movie.presentation.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

import com.example.mealplanner.R
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.presentation.navigation.AppDestinations
import com.example.mealplanner.presentation.navigation.AppNavigation

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val recentlyUpdatedMovies by viewModel.recentlyUpdatedMovies.collectAsState()

    when (recentlyUpdatedMovies) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text(
            (recentlyUpdatedMovies as Resource.Error).message,
            color = MaterialTheme.colorScheme.error
        )

        is Resource.Success -> {
            FeaturedBannerPager((recentlyUpdatedMovies as Resource.Success).data.take(10)) { slug ->
                navController.navigate("${AppDestinations.MOVIE_DETAIL_ROUTE_BASE}/${slug}")
            }
        }
    }
}

@Composable
fun FeaturedBannerPager(movies: List<Movie>, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { movies.size })

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            contentPadding = PaddingValues(horizontal = 48.dp), // Adjust this value as needed for more/less peeking
            pageSpacing = 16.dp,  // Optional: Add spacing between the pages themselves
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val movie = movies[page]


            Box(
                modifier = Modifier
                    // .fillMaxWidth() // Assuming this is part of your item's modifier
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        onClick(movie.metadata.slug)
                    }, // Clip the outer Box
                contentAlignment = Alignment.Center
            ) {
                // Background Layer: Blurred and fills the entire space
                AsyncImage(
                    model = movie.metadata.thumb_url,
                    contentDescription = null, // Decorative, as it's a background
                    contentScale = ContentScale.Crop, // Crop will ensure it fills the bounds
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(radius = 25.dp), // Adjust radius as needed.
                    // Note: .blur() is simpler on Android 12+ (API 31+).
                    // For older APIs, you might need a Coil Transformation for blur.
                    alpha = 0.7f // Optional: Make the background slightly transparent
                )

                // Foreground Layer: Your main image with ContentScale.Fit
                AsyncImage(
                    model = movie.metadata.thumb_url,
                    contentDescription = movie.metadata.name,
                    contentScale = ContentScale.Fit, // Shows the full image, letterboxed if needed
                    modifier = Modifier.fillMaxSize(), // It will fit within this Box
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_background)
                )
            }
        }

        PagerIndicator(
            totalDots = movies.size,
            selectedIndex = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }

}

@Composable
fun PagerIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = Color.Gray.copy(alpha = 0.5f),
    dotHeight: Dp = 8.dp,
    dotWidth: Dp = 8.dp, // Width for unselected dots
    selectedDotHeight: Dp = 8.dp, // Height for the selected dot
    selectedDotWidth: Dp = 24.dp, // Width for the selected dot (e.g., wider)
    cornerRadius: Dp = 4.dp, // Corner radius for the rectangle
    spacing: Dp = 8.dp // Space between dots
) {
    Row(
        // This will place items with 'spacing' between them,
        // and then center the whole group horizontally within the Row.
        // The Row itself should be fillMaxWidth or centered by its parent.
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth() // Ensure Row takes up width to center content
        // or use .wrapContentWidth() and let parent center.
        // Given original was Arrangement.Center, fillMaxWidth + spacedBy a good combo.
    ) {
        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex

            val animatedHeight by animateDpAsState(
                targetValue = if (isSelected) selectedDotHeight else dotHeight,
                label = "DotHeightAnimation"
            )
            val animatedWidth by animateDpAsState(
                targetValue = if (isSelected) selectedDotWidth else dotWidth,
                label = "DotWidthAnimation"
            )
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) selectedColor else unselectedColor,
                label = "DotColorAnimation"
            )

            Box(
                modifier = Modifier
                    .height(animatedHeight)
                    .width(animatedWidth)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(animatedColor)
            )
        }
    }
}