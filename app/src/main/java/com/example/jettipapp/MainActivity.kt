package com.example.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.IconbuttonSizeModifier
import com.example.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                //TopHeader()
                MainContent()
            }
        }
    }
}
//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0){
        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
            , color = Color(0xFFE9D7F7)
        ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
            val total = "%.2f".format(totalPerPerson)
            Text(text= "Total Per Person",style = MaterialTheme.typography.h5)
            Text(text= "$${total}",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold)

        }
    }

}
@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent(){
    val splitState = remember{ mutableStateOf(1)}
    val range = IntRange(start = 1, endInclusive = 100)
    val tipAmountState = remember{ mutableStateOf(0.0)}
    val totalPerPersonState = remember { mutableStateOf(0.0)}
    Column(modifier = Modifier.padding(12.dp)) {
        BillForm(splitState = splitState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState)
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             range: IntRange = 1..100,
             splitState: MutableState<Int>,
             tipAmountState: MutableState<Double>,
             totalPerPersonState: MutableState<Double>,
onValChange: (String) -> Unit = {}){
    val totalBillState = remember { mutableStateOf("")}
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(modifier = modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(6.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
            InputField(valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if(!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                })
            if(validState){
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start){
                    Text("Split",
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))
                    Spacer(modifier = modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End){
                    RoundIconButton(imageVector = Icons.Default.Remove, onClick = {

                        if(splitState.value > 1) splitState.value -= 1
                        else splitState.value = 1
                        calculateTotalTip(totalBillState.value.toDouble(),
                            (sliderPositionState.value * 100).toInt())
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBillState.value.toDouble(),
                                splitState.value,
                                (sliderPositionState.value * 100).toInt())

                    })
                        Text(text = splitState.value.toString(),
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 9.dp))
                    RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                        if(splitState.value < range.last)
                        splitState.value += 1
                        calculateTotalTip(totalBillState.value.toDouble(),
                            (sliderPositionState.value * 100).toInt())
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBillState.value.toDouble(),
                                splitState.value,
                                (sliderPositionState.value * 100).toInt())
                    })
                    }
                }
            //Tip Row
            Row(modifier = modifier.padding(horizontal = 3.dp, vertical = 12.dp)
                ){
                Text(text = "Tip", modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(200.dp))
                Text(text = "$${tipAmountState.value}", modifier = Modifier.align(Alignment.CenterVertically))
            }
            //Slider column
            Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${(sliderPositionState.value * 100).toInt()}%")
                Spacer(modifier = Modifier.height(14.dp))
                Slider(value = sliderPositionState.value,
                    onValueChange = { newVal ->
                        sliderPositionState.value = newVal
                        tipAmountState.value =
                            calculateTotalTip(totalBillState.value.toDouble(),
                                (sliderPositionState.value * 100).toInt())
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBillState.value.toDouble(),
                                splitState.value,
                                (sliderPositionState.value * 100).toInt())
                }, steps = 5,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp))
            }
        } else{
                Box(){

                }
            }
        }


    }

}




