package com.example.camera

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
private val showProcess = mutableStateOf(false)

@Composable
fun ImagePanel(
    bitmaps: List<Bitmap>,
    modifier: Modifier = Modifier

) {

    if (bitmaps.isEmpty()) {
        Box(
            modifier = modifier
                .padding(16.dp),
            contentAlignment = Alignment.Center

        ) {
            Text("No Photos taken")
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
        ) {
            items(bitmaps) { bitmaps ->
                if(showProcess.value) {
                    ImageProcessing(bitmap = bitmaps, modifier = Modifier)
                }
                IconButton(onClick = {
                    Log.e("Camera", "Picture pressed");
                    
                    showProcess.value = true

                },
                    modifier = Modifier.size(200.dp)) {


                    androidx.compose.foundation.Image(
                        bitmap = bitmaps.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp)),


                        )
                }

            }
        }
    }
}

@Composable
fun ImageProcessing(bitmap: Bitmap,
                    modifier: Modifier = Modifier) {
    val w: Int = bitmap.getWidth()
    val h: Int = bitmap.getHeight()

    val pixelValue = bitmap.getPixel( w/2-8, h/2)
    val whitePixelValue = bitmap.getPixel(w/2-8, h/2+80)
    val R: Int = pixelValue shr 16 and 0xff
    val G: Int = pixelValue shr 8 and 0xff
    val B: Int = pixelValue and 0xff

    val RW: Int = whitePixelValue shr 16 and 0xff
    val GW: Int = whitePixelValue shr 8 and 0xff
    val BW: Int = whitePixelValue and 0xff

    val remainingR = 255-RW
    val remainingG = 255-GW
    val remainingB = 255-BW

    val recalR = R+remainingR
    val recalB = R+remainingB
    val recalG = R+remainingG
            //
    val stringR = R.toString()
    val stringG = G.toString()
    val stringB = B.toString()

    val stringRW = RW.toString()
    val stringGW = GW.toString()
    val stringBW = BW.toString()
    val stringValue = stringR + "," + stringG + "," + stringB
    val stringValueW = stringRW + "," + stringGW + "," + stringBW
    val toast = Toast.makeText(LocalContext.current,stringValue + "||" + stringValueW,Toast.LENGTH_LONG)
//
    toast.show()
    Log.e("RGB",stringValue)

}



