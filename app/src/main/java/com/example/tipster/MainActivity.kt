package com.example.tipster

import android.os.Bundle
import android.provider.CalendarContract.Instances
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipster.ui.theme.TipsterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipsterTheme {
                App()
            }
        }
    }
}
@Composable
fun App(modifier: Modifier = Modifier) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background){
        TipCalculator()
    }
    }

@Composable
fun TipCalculator() {
    val amount = remember {
        mutableStateOf("")
    }
    val personCounter = remember {
        mutableIntStateOf(1)
    }
    val tipPercentage = remember {
        mutableFloatStateOf(5f)
    }

    Column (modifier = Modifier.fillMaxWidth()){
        TotalHeader(amount = TotalPerPerson(amount.value, tipPercentage.floatValue, personCounter.intValue))
        UserInputArea(
            amount = amount.value,
            amountChange = {amount.value = it},
            personCounter = personCounter.intValue,
            onAddorReducePerson =
            {if(it<0){
                if (personCounter.intValue != 1){
                    personCounter.intValue --
                }
            }else{
                personCounter.intValue++
            }
                 },
            tipPercentage = tipPercentage.floatValue
        ) {
            tipPercentage.floatValue = it
        }
    }
}

@Composable
fun TotalHeader(amount: String) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(30.dp), shadowElevation = 5.dp,
        color = colorResource(id = R.color.cyan),
        shape = RoundedCornerShape(9.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text ="Total Per Person",
                style = TextStyle(color = Color.Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(5.dp))
            Text(text ="Rs. $amount",
                style = TextStyle(color = Color.Black,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun CustomButton(imageVector: ImageVector, onClick: () -> Unit){
    Card(modifier = Modifier
        .wrapContentSize(align = Alignment.Center)
        .padding(4.dp)
        .clickable {
            onClick.invoke()
        }, shape = CircleShape
    ) {
        Icon(imageVector = imageVector, contentDescription = null, modifier = Modifier.size(30.dp))
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserInputArea(amount: String,
                  amountChange:(String)->Unit,
                  personCounter:Int,
                  onAddorReducePerson:(Int)->Unit,
                  tipPercentage: Float,
                  ontipChange: (Float) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val showDesc  = remember {
        mutableStateOf(false)
    }
    showDesc.value = amount.isNotEmpty()
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(5.dp),
        shadowElevation = 14.dp
    ) {
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
            OutlinedTextField(value = amount, onValueChange = { amountChange.invoke(it) },
                modifier = Modifier.fillMaxWidth(),

                textStyle = TextStyle(color = Color.Blue),
                placeholder = { Text(text = "Enter you bill amount" ) },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = {
                    keyboardController?.hide()
                })
                    )
            Spacer(modifier = Modifier.height(10.dp))
            if(showDesc.value){
                
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Split", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.fillMaxWidth(.60f))
                CustomButton(imageVector = Icons.Default.KeyboardArrowUp) {
                    onAddorReducePerson.invoke(1)
                }
                Text(text = "$personCounter", style = MaterialTheme.typography.bodyMedium)
                CustomButton(imageVector = Icons.Default.KeyboardArrowDown) {
                    onAddorReducePerson.invoke(-1)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tip", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.fillMaxWidth(.70f))
                Text(text = "Rs.${getTipAmount(amount, tipPercentage)}", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text(text = "$tipPercentage %", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(10.dp))
            Slider(value = tipPercentage, onValueChange = {
                ontipChange.invoke(it)
            }, valueRange = 0f..100f, steps = 9,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp))
        }}
    }
}
fun getTipAmount(userAmount: String, tipPercentage: Float):String{
    return when{
        userAmount.isEmpty()->{"0"}
        else -> {
            val amount = userAmount.toFloat()
           (amount*tipPercentage.div(100)).toString()
        }        }
}
fun TotalPerPerson(amount: String, tipPercentage: Float, personCounter: Int):String {
    return when {
        amount.isEmpty() -> {
            "0"
        }

        else -> {
            val amountFloat = amount.toFloat()
            val tipAmount = amountFloat * tipPercentage.div(100)
            val perPerson = ((amountFloat + tipAmount).div(personCounter))
            perPerson.toString()
        }
    }
}


