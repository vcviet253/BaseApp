@file:OptIn(ExperimentalFoundationApi::class)

package com.example.mealplanner.movie.presentation.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mealplanner.R
import com.example.mealplanner.movie.domain.model.Movie
import com.example.mealplanner.presentation.navigation.AppDestinations
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import com.google.accompanist.placeholder.placeholder
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    // Collecting states
    val recentlyUpdatedState by viewModel.recentlyUpdatedState.collectAsState()
    val movieStates by viewModel.movieStates.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Recently Updated
            when {
                recentlyUpdatedState.isLoading -> {
                    RecentlyUpdatedMoviesShimmerPagerPlaceHolder()
                }

                recentlyUpdatedState.error.isNotEmpty() -> {
                    Text(
                        recentlyUpdatedState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    RecentlyUpdatedMoviesPager(
                        recentlyUpdatedState.movies.take(5)
                    ) { slug ->
                        navController.navigate("${AppDestinations.MOVIE_DETAIL_ROUTE_BASE}/${slug}")
                    }
                }
            }
        }

        // Movies by Category (Hành Động, Hài, Tình Cảm)
        items(listOf("hoc-duong", "gia-dinh", "tinh-cam"), key = { it }) { category ->
            val categoryState = movieStates[category]

            Text(
                category.replace("-", " ").replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium
            )
            when {
                categoryState == null || categoryState.isLoading -> {
                    ShimmerMovieRowList()
                }

                categoryState.error.isNotEmpty() -> {
                    Text(
                        categoryState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    MovieListByCategory(
                        category,
                        categoryState.movies,
                        onClick = { slug ->
                            navController.navigate("${AppDestinations.MOVIE_DETAIL_ROUTE_BASE}/$slug")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MovieListByCategory(
    type: String,
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(movies) { movie ->
            MoviePosterCard(
                movie,
                onClick
            )
        }
    }
}

@Composable
fun MoviePosterCard(movie: Movie, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp) // Adjust width as needed
            .height(180.dp) // Adjust height as needed
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onClick(movie.metadata.slug)
            }
    ) {
        var imageLoaded by remember { mutableStateOf(false) }

        if (!imageLoaded) {
            ShimmerMoviePosterCard()
        }

        AsyncImage(
            //  model = movie.metadata.poster_url,
            model = "https://phimimg.com/${movie.metadata.poster_url.removePrefix("/")}",
            contentDescription = movie.metadata.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            error = painterResource(id = R.drawable.ic_launcher_background), // Replace with your error image
            onSuccess = { imageLoaded = true }
        )

        if (imageLoaded) {
            // Optional: Add a gradient overlay or movie title at the bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = movie.metadata.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.fillMaxWidth().basicMarquee(
                        animationMode = MarqueeAnimationMode.Immediately, // hoặc WhileFocused
                        repeatDelayMillis = 1800, //độ trễ trước vong lap tiep theo
                        spacing = MarqueeSpacing(48.dp), // khoảng cách giữa 2 lần lặp
                        velocity = 30.dp // tốc độ: dp per second
                    )
                )
            }
        }
    }
}

@Composable
fun ShimmerMoviePosterCard() {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(MaterialTheme.shapes.medium)
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.shimmer(),
                color = Color.Gray,
                shape = MaterialTheme.shapes.medium
            )
    )
}

@Composable
fun ShimmerMovieRowList() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(5) { // Giả lập 5 item shimmer
            ShimmerMoviePosterCard()
        }
    }
}

@Composable
fun RecentlyUpdatedMoviesPager(
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = movies.size / 2, pageCount = { movies.size })

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 2,
            contentPadding = PaddingValues(horizontal = 64.dp), // Adjust this value as needed for more/less peeking
            pageSpacing = -40.dp,  // Optional: Add spacing between the pages themselves
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val movie = movies[page]
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val clampedOffset = pageOffset.coerceIn(-1f, 1f)

            // Apply exaggerated visual effect for outer pages
            val visualOffset = if (clampedOffset.absoluteValue < 0.001f) 0f else clampedOffset
            Box(
                modifier = Modifier
                    // .fillMaxWidth() // Assuming this is part of your item's modifier
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        onClick(movie.metadata.slug)
                    } // Clip the outer Box
                    //Tao hieu ung nghieng giong cua so
                    .graphicsLayer {
                        val scale = lerp(0.8f, 1f, 1f - visualOffset.absoluteValue)
                        scaleX = scale
                        scaleY = scale

                        val rotation = lerp(45f, 0f, 1f - visualOffset.absoluteValue)
                        rotationY =
                            -visualOffset * rotation  //Neu muon nghieng vao trong, bo dau - di


                        alpha = lerp(0.4f, 1f, 1f - visualOffset.absoluteValue)
                        cameraDistance = 16 * density
                    },
                contentAlignment = Alignment.Center
            ) {
                var imageLoaded by remember { mutableStateOf(false) }

                if (!imageLoaded) {
                    ShimmerBox()
                }

                if (imageLoaded) {
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
                        alpha = 0.7f, // Optional: Make the background slightly transparent
                    )
                }

                // Foreground Layer: Your main image with ContentScale.Fit
                AsyncImage(
                    model = movie.metadata.thumb_url,
                    contentDescription = movie.metadata.name,
                    contentScale = ContentScale.Fit, // Shows the full image, letterboxed if needed
                    modifier = Modifier.fillMaxSize(), // It will fit within this Box
                    error = painterResource(id = R.drawable.ic_launcher_background),
                    onSuccess = { imageLoaded = true }
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

@Composable
fun RecentlyUpdatedMoviesShimmerPagerPlaceHolder(
    size: Int = 5,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = size / 2, pageCount = { size })

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 2,
            contentPadding = PaddingValues(horizontal = 64.dp), // Adjust this value as needed for more/less peeking
            pageSpacing = -40.dp,  // Optional: Add spacing between the pages themselves
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val pageOffset =
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val clampedOffset = pageOffset.coerceIn(-1f, 1f)

            // Apply exaggerated visual effect for outer pages
            val visualOffset = if (clampedOffset.absoluteValue < 0.001f) 0f else clampedOffset
            Box(
                modifier = Modifier
                    // .fillMaxWidth() // Assuming this is part of your item's modifier
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    // Clip the outer Box
                    //Tao hieu ung nghieng giong cua so
                    .graphicsLayer {
                        val scale = lerp(0.8f, 1f, 1f - visualOffset.absoluteValue)
                        scaleX = scale
                        scaleY = scale

                        val rotation = lerp(45f, 0f, 1f - visualOffset.absoluteValue)
                        rotationY =
                            -visualOffset * rotation  //Neu muon nghieng vao trong, bo dau - di


                        alpha = lerp(0.4f, 1f, 1f - visualOffset.absoluteValue)
                        cameraDistance = 16 * density
                    },
                contentAlignment = Alignment.Center
            ) {
                ShimmerBox()
            }
        }

        PagerIndicator(
            totalDots = size,
            selectedIndex = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}


@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.shimmer(),
                color = Color.Gray,
                shape = MaterialTheme.shapes.medium
            )
    )
}