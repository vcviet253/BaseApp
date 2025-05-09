package com.example.mealplanner.presentation.weather

import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mealplanner.R
import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.domain.model.weather.WeatherInfo
import com.example.mealplanner.ui.theme.Purple100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val weatherState by viewModel.weatherInfoState.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            when (weatherState) {
                is Resource.Success -> {
                    val weatherInfo = (weatherState as Resource.Success).data
                    // WeatherBottomSheetContent(weatherInfo)
                    // Danh sách dữ liệu mẫu (thay thế bằng dữ liệu thực tế của bạn)
                    val sampleWeatherDataItems = listOf(
                        WeatherGridItem(1, "UV INDEX", Icons.Default.WbSunny, "4", "Moderate"),
                        WeatherGridItem(
                            2,
                            "SUNRISE",
                            Icons.Default.WbTwilight,
                            "5:28 AM",
                            "Sunset: 7:25PM"
                        ),
                        WeatherGridItem(3, "WIND", Icons.Default.Air, "9.7 km/h", null),
                        WeatherGridItem(
                            4,
                            "RAINFALL",
                            Icons.Default.Grain,
                            "1.8 mm",
                            "in last hour"
                        ), // Dùng icon Grain hoặc WaterDrop
                        WeatherGridItem(
                            5,
                            "FEELS LIKE",
                            Icons.Default.Thermostat,
                            "19°",
                            "Similar to actual..."
                        ),
                        WeatherGridItem(
                            6,
                            "HUMIDITY",
                            Icons.Default.Opacity,
                            "90%",
                            "The dew point is..."
                        ),
                        WeatherGridItem(
                            7,
                            "VISIBILITY",
                            Icons.Default.Visibility,
                            "8 km",
                            "Similar to actual..."
                        ),
                        WeatherGridItem(
                            8,
                            "PRESSURE",
                            Icons.Default.Compress,
                            "980 hPa",
                            null
                        ) // Dùng icon Compress hoặc Speed
                    )
                    WeatherGrid(sampleWeatherDataItems)

                }

                else -> CircularProgressIndicator()
            }
        },
        sheetPeekHeight = 80.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.weather_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            when (weatherState) {
                is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Resource.Error -> Text(
                    (weatherState as Resource.Error).message,
                    modifier = Modifier.align(Alignment.Center)
                )

                is Resource.Success -> {
                    val weatherInfo = (weatherState as Resource.Success).data
                    WeatherBackgroundContent(weatherInfo)
                }
            }
        }
    }
}

@Composable
fun WeatherBackgroundContent(weatherInfo: WeatherInfo) {
    TemperatureMainCard(weatherInfo)
}

@Composable
fun WeatherBottomSheetContent(weatherInfo: WeatherInfo) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF5936B4), Color(0xFF362A84))
                )
            )
    )
}

@Composable
fun WeatherGrid(items: List<WeatherGridItem>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Chỉ định lưới có 2 cột cố định
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 8.dp
        ), // Padding cho toàn bộ lưới
        verticalArrangement = Arrangement.spacedBy(8.dp), // Khoảng cách giữa các hàng
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Khoảng cách giữa các cột
    ) {
        items(items = items, key = { it.id }) { item -> // Dùng key để tối ưu
            WeatherDetailCard(item = item)
        }
    }
}

// 2. Composable để hiển thị một ô trong lưới (Ví dụ đơn giản)
@Composable
fun WeatherDetailCard(item: WeatherGridItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth() // Card sẽ chiếm hết chiều rộng của ô grid
            .aspectRatio(1f), // Giữ tỉ lệ vuông cho card (có thể điều chỉnh)
        shape = RoundedCornerShape(16.dp), // Bo góc
        // colors = CardDefaults.cardColors(containerColor = ...) // Đặt màu nền tối
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Lấp đầy card
                .padding(12.dp), // Padding bên trong card
        ) {
            // Hàng trên: Icon và Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(item.icon, contentDescription = item.title, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    // color = Color.Gray // Màu chữ phụ
                )
            }

            Text(
                text = item.value,
                fontSize = 24.sp, // Kích thước lớn cho giá trị chính
                fontWeight = FontWeight.Bold,
                // color = Color.White
            )

            // Nội dung chính (Value và Description)

            item.description?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    // color = Color.LightGray // Màu chữ mô tả
                )
            }


            // TODO: Thêm các thành phần đặc biệt dựa vào item.type
            // Ví dụ:
            // if (item.title == "UV INDEX" && item.progress != null) {
            //     LinearProgressIndicator(progress = item.progress, modifier = Modifier.fillMaxWidth())
            // }
            // if (item.title == "PRESSURE") {
            //     // Vẽ gauge bằng Canvas ở đây
            // }
            // if (item.title == "SUNRISE") {
            // Vẽ biểu đồ ở đây
            // }
            // if (item.title == "WIND") {
            // Vẽ la bàn ở đây
            // }
        }
    }
}

data class WeatherGridItem(
    val id: Int,
    val title: String,
    val icon: ImageVector, // Dùng ImageVector từ Material Icons
    val value: String,
    val description: String? = null,
    // Thêm các trường khác nếu cần, ví dụ: progress, type để biết vẽ gì đặc biệt
    // val type: String = "default",
    // val progress: Float? = null
)

@Composable
fun TemperatureMainCard(weatherInfo: WeatherInfo) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        //Location
        Text(weatherInfo.location.name, style = MaterialTheme.typography.bodyLarge)

        //Temperature
        val tempText = stringResource(R.string.temperature_celsius, weatherInfo.currentWeather.temperatureC)
        Text(weatherInfo.currentWeather.temperatureC.toString() + "\u00B0", style = MaterialTheme.typography.headlineLarge)

        Text(weatherInfo.currentWeather.condition.text, color = Color.Gray)
    }
}