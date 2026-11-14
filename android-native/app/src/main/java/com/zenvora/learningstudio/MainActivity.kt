package com.zenvora.learningstudio

import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

private val Navy=Color(0xFF071321)
private val Panel=Color(0xFF101F32)
private val Cyan=Color(0xFF00E5FF)
private val Teal=Color(0xFF14BBA6)
private val Muted=Color(0xFF9DB0C5)
private val Ink=Color(0xFFF4F8FF)
data class Course(val title:String,val subtitle:String,val level:String,val icon:String,val lessons:Int)
private val courses=listOf(
 Course("CapCut Editing","From first cut to polished short-form video","CREATIVE","🎬",24),
 Course("KineMaster","Mobile editing, layers, transitions and audio","MOBILE","📱",22),
 Course("Alight Motion","Keyframes, motion graphics and visual effects","MOTION","✦",25),
 Course("Node Video Editor","Advanced mobile editing and creative effects","ADVANCED","🎞️",24),
 Course("Filmora PC Editor","A practical desktop editing workflow","DESKTOP","🖥️",28),
 Course("DaVinci Resolve","Professional editing, colour and finishing","PRO STUDIO","🎨",30)
)
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);MobileAds.initialize(this){};setContent{ZenvoraApp()}}}

@Composable fun ZenvoraApp(){
 var tab by remember{mutableStateOf("Home")};var selected by remember{mutableStateOf<Course?>(null)}
 val context=androidx.compose.ui.platform.LocalContext.current
 val prefs=remember{context.getSharedPreferences("zenvora_v1",android.content.Context.MODE_PRIVATE)}
 var showAuth by remember{mutableStateOf(false)}
 var user by remember{mutableStateOf(prefs.getString("user_email","")?: "")}
 var progress by remember{mutableIntStateOf(prefs.getInt("progress",0))}
 MaterialTheme(colorScheme=darkColorScheme(primary=Cyan,secondary=Teal,background=Navy,surface=Panel,onPrimary=Navy,onBackground=Ink,onSurface=Ink)){
  Surface(Modifier.fillMaxSize(),color=Navy){Column{
   TopBar{showAuth=true}
   when{showAuth->AuthScreen({showAuth=false},{user=it;prefs.edit().putString("user_email",it).apply();showAuth=false})
    selected!=null->CourseScreen(selected!!,{selected=null},{progress=(progress+2).coerceAtMost(100);prefs.edit().putInt("progress",(progress+2).coerceAtMost(100)).apply()})
    tab=="Courses"->CourseList{selected=it}
    tab=="Progress"->ProgressScreen(progress,user)
    tab=="Profile"->ProfileScreen(user){showAuth=true}
    tab=="AI Tutor"->AiTutorScreen()
    else->HomeScreen(progress,{tab="Courses"},{selected=it})}
   Spacer(Modifier.weight(1f))
   BannerAd()
   NavigationBar(containerColor=Panel,contentColor=Ink){
    listOf(Triple("Home",Icons.Filled.Home,"Home"),Triple("Courses",Icons.Filled.MenuBook,"Courses"),Triple("AI Tutor",Icons.Filled.SmartToy,"AI Tutor"),Triple("Progress",Icons.Filled.Insights,"Progress"),Triple("Profile",Icons.Filled.Person,"Profile")).forEach{(label,icon,dest)->
     NavigationBarItem(selected=tab==dest&&selected==null&&!showAuth,onClick={selected=null;showAuth=false;tab=dest},icon={Icon(icon,null)},label={Text(label)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Cyan,indicatorColor=Color(0xFF123B4A),selectedTextColor=Cyan,unselectedTextColor=Muted))
    }
   }
  }}
 }
}
@Composable private fun TopBar(onAccount:()->Unit){Row(Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=14.dp),verticalAlignment=Alignment.CenterVertically){
 Row(Modifier.weight(1f),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(42.dp).background(Brush.linearGradient(listOf(Cyan,Teal)),RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){Text("Z",color=Navy,fontSize=29.sp,fontWeight=FontWeight.Black)}
 Spacer(Modifier.width(10.dp));Column{Text("ZENVORA",fontSize=20.sp,fontWeight=FontWeight.ExtraBold,letterSpacing=1.5.sp);Text("LEARNING PLATFORM",fontSize=8.sp,color=Cyan,letterSpacing=2.sp)}}
 IconButton(onClick=onAccount){Icon(Icons.Filled.AccountCircle,null,tint=Cyan)}
}}
@Composable private fun HomeScreen(progress:Int,onCourses:()->Unit,onSelect:(Course)->Unit){LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
 item{Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF103C4A),Panel,Navy)),RoundedCornerShape(24.dp)).border(1.dp,Color(0xFF28536A),RoundedCornerShape(24.dp)).padding(22.dp)){
 Text("YOUR LEARNING SPACE",color=Cyan,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=3.sp);Spacer(Modifier.height(18.dp))
 Text("Learn skills.\nCreate more.\nGo further.",fontSize=32.sp,fontWeight=FontWeight.ExtraBold,lineHeight=38.sp);Spacer(Modifier.height(10.dp))
 Text("Practical courses to help you build real-world creative skills.",color=Muted);Spacer(Modifier.height(20.dp))
 Button(onClick=onCourses,colors=ButtonDefaults.buttonColors(containerColor=Cyan,contentColor=Navy),shape=RoundedCornerShape(12.dp)){Text("Explore courses →",fontWeight=FontWeight.Bold)}
 }}
 item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("06","Courses",Modifier.weight(1f));StatCard(progress.toString()+"%","Progress",Modifier.weight(1f));StatCard("∞","Grow",Modifier.weight(1f))}}
 item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){Text("Continue learning",fontSize=21.sp,fontWeight=FontWeight.Bold);TextButton(onClick=onCourses){Text("View all →",color=Cyan)}}}
 items(courses.take(3)){CourseCard(it){onSelect(it)}}
}}
@Composable private fun StatCard(value:String,label:String,modifier:Modifier=Modifier){Column(modifier.background(Panel,RoundedCornerShape(16.dp)).border(1.dp,Color(0xFF263F58),RoundedCornerShape(16.dp)).padding(13.dp)){Text(value,fontSize=24.sp,fontWeight=FontWeight.ExtraBold);Text(label,color=Muted,fontSize=11.sp)}}
@Composable private fun CourseList(onSelect:(Course)->Unit){LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(13.dp)){item{Text("Your learning paths",fontSize=27.sp,fontWeight=FontWeight.Bold);Text("Choose a course and build a new skill.",color=Muted)};items(courses){CourseCard(it){onSelect(it)}}}}
@Composable private fun CourseCard(course:Course,onClick:()->Unit){Card(onClick=onClick,colors=CardDefaults.cardColors(containerColor=Panel),shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,Color(0xFF29435C))){Row(Modifier.fillMaxWidth().padding(17.dp),verticalAlignment=Alignment.CenterVertically){
 Box(Modifier.size(54.dp).background(Color(0xFF0B3441),RoundedCornerShape(15.dp)),contentAlignment=Alignment.Center){Text(course.icon,fontSize=25.sp)}
 Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(course.level,color=Cyan,fontSize=9.sp,fontWeight=FontWeight.Bold,letterSpacing=1.5.sp);Text(course.title,fontWeight=FontWeight.Bold,fontSize=17.sp);Text(course.subtitle,color=Muted,fontSize=12.sp);Text(course.lessons.toString()+" lessons • Start learning →",color=Cyan,fontSize=11.sp)}
 Icon(Icons.Filled.ChevronRight,null,tint=Muted)
}}}
@Composable private fun CourseScreen(course:Course,onBack:()->Unit,onComplete:()->Unit){
 val context=androidx.compose.ui.platform.LocalContext.current
 val prefs=remember{context.getSharedPreferences("zenvora_v1",android.content.Context.MODE_PRIVATE)}
 val progressKey="completed_"+course.title.replace(" ","_")
 var completed by remember(course.title){mutableStateOf(prefs.getString(progressKey,"").orEmpty().split(",").filter{it.isNotBlank()}.mapNotNull{it.toIntOrNull()}.toSet())}
 val stages=when(course.title){
 "CapCut Editing"->listOf("Interface, projects & media","Timeline editing & precision cuts","Text, captions & typography","Transitions, effects & overlays","Audio, voiceover & sound design","Speed ramps, keyframes & tracking","Colour, export & quality control","Final short-form portfolio project")
 "KineMaster"->listOf("Workspace setup & media import","Timeline editing fundamentals","Layers, titles & chroma key","Keyframes, animation & transitions","Audio mixing & sound design","Effects, compositing & colour","Export settings & optimisation","Client-ready portfolio project")
 "Alight Motion"->listOf("Projects, layers & interface","Keyframe fundamentals & easing","Text, shapes & vector animation","Effects, masks & compositing","Advanced motion & camera","Tracking, polish & visual rhythm","Export, frame rate & optimisation","Motion graphics portfolio")
 "Node Video Editor"->listOf("Interface, nodes & project setup","Editing basics & media workflow","Keyframes, curves & motion","Effects, colour & adjustments","Compositing and advanced layers","Motion design & visual effects","Render settings & quality checks","Advanced portfolio challenge")
 "Filmora PC Editor"->listOf("Workspace, media & project setup","Timeline editing fundamentals","Titles, transitions & overlays","Audio, voice & sound cleanup","Colour correction & speed effects","Advanced editing workflow","Export, codecs & quality control","Portfolio and client delivery")
 else->listOf("DaVinci interface, projects & media","Cut and Edit page fundamentals","Timeline, trimming & finishing","Colour correction and grading","Fusion titles, motion graphics & VFX","Fairlight audio post-production","Delivery, codecs & workflow","Professional portfolio project")
 }
 val videoIds=mapOf("KineMaster" to "9BouKFVvMtc","Alight Motion" to "EH8vlQNbONQ","Node Video Editor" to "aUoYQHutvWg","Filmora PC Editor" to "7I1Xmyu0gzM","DaVinci Resolve" to "jqQF44f3NFE")
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)){
  TextButton(onClick=onBack){Text("← All courses",color=Cyan)}
  Text(course.level,color=Cyan,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
  Text(course.icon,fontSize=42.sp);Text(course.title,fontSize=29.sp,fontWeight=FontWeight.ExtraBold)
  Text(course.subtitle,color=Muted);Spacer(Modifier.height(16.dp))
  Surface(color=Panel,shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,Color(0xFF29435C))){
   Column(Modifier.padding(18.dp)){
    Text("YOUR LEARNING PATH",color=Cyan,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
    Text("8 structured modules • Beginner to Pro",fontSize=18.sp,fontWeight=FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
    LinearProgressIndicator(progress={completed.size/8f},modifier=Modifier.fillMaxWidth(),color=Cyan,trackColor=Color(0xFF263F58))
    Text(completed.size.toString()+" of 8 modules completed",color=Muted,fontSize=12.sp)
    stages.forEachIndexed{i,stage->
     Row(Modifier.fillMaxWidth().clickable{
      val videoId=videoIds[course.title]
      if(videoId!=null) context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW,android.net.Uri.parse("https://www.youtube.com/watch?v="+videoId)))
     }.padding(vertical=13.dp),verticalAlignment=Alignment.CenterVertically){
      Text((i+1).toString().padStart(2,'0'),color=Cyan,fontWeight=FontWeight.Bold);Spacer(Modifier.width(14.dp))
      Column(Modifier.weight(1f)){Text(stage,fontWeight=FontWeight.SemiBold);Text(if(videoIds.containsKey(course.title))"Open lesson video ↗" else "Study module and complete task",color=Muted,fontSize=11.sp)}
      Icon(if(i in completed)Icons.Filled.CheckCircle else Icons.Filled.PlayCircleOutline,null,tint=if(i in completed)Teal else Cyan)
     }
     HorizontalDivider(color=Color(0xFF263F58))
     Row(Modifier.fillMaxWidth().padding(vertical=4.dp),horizontalArrangement=Arrangement.End){
      TextButton(onClick={if(i !in completed){completed=completed+i;prefs.edit().putString(progressKey,completed.sorted().joinToString(",")).apply();onComplete()}}){Text(if(i in completed)"Completed ✓" else "Mark complete",color=if(i in completed)Teal else Cyan)}
     }
    }
    Spacer(Modifier.height(10.dp));Text("PRACTICAL CHALLENGE",color=Cyan,fontWeight=FontWeight.Bold,fontSize=12.sp)
    Text("Create a complete "+course.title+" project using the skills from all eight modules. Export it in the correct format and review the final quality.",color=Muted)
   }
  }
 }
}
@Composable private fun ProgressScreen(progress:Int,email:String){Column(Modifier.fillMaxSize().padding(22.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
 Text("Your progress",fontSize=28.sp,fontWeight=FontWeight.Bold);Text("Keep showing up. Small steps add up.",color=Muted);StatCard(progress.toString()+"%","Learning activity")
 LinearProgressIndicator(progress={progress/100f},modifier=Modifier.fillMaxWidth(),color=Cyan,trackColor=Panel)
 Text(if(progress==0)"Start your first course to build your progress." else "Great start. Continue learning to improve your progress.")
 Text("Signed in as "+email.ifBlank{"Guest"},color=Muted,fontSize=12.sp)
}}
@Composable private fun ProfileScreen(email:String,onLogin:()->Unit){val context=androidx.compose.ui.platform.LocalContext.current
 Column(Modifier.fillMaxSize().padding(22.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
 Text("Your profile",fontSize=28.sp,fontWeight=FontWeight.Bold);Surface(color=Panel,shape=RoundedCornerShape(18.dp)){Column(Modifier.padding(18.dp)){Icon(Icons.Filled.AccountCircle,null,tint=Cyan,modifier=Modifier.size(48.dp));Text(email.ifBlank{"Guest learner"},fontWeight=FontWeight.Bold,fontSize=18.sp);Text(if(email.isBlank())"Sign in to connect your account" else "Zenvora learner",color=Muted)}}
 Button(onClick=onLogin,colors=ButtonDefaults.buttonColors(containerColor=Cyan,contentColor=Navy)){Text(if(email.isBlank())"Log in / Sign up" else "Account settings")}
 if(email.isNotBlank()) OutlinedButton(onClick={context.getSharedPreferences("zenvora_v1",android.content.Context.MODE_PRIVATE).edit().remove("user_email").apply();onLogin()},modifier=Modifier.fillMaxWidth()){Text("Sign out",color=Cyan)}
 Text("Your module completion and learning progress are saved on this device.",color=Muted,fontSize=11.sp)
 Text("Zenvora • Learn. Create. Earn.",color=Muted,fontSize=12.sp)
}}
@Composable private fun AuthScreen(onBack:()->Unit,onSignedIn:(String)->Unit){var email by remember{mutableStateOf("")};var password by remember{mutableStateOf("")};var signup by remember{mutableStateOf(false)};var busy by remember{mutableStateOf(false)};var message by remember{mutableStateOf("")};val scope= rememberCoroutineScope()
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 TextButton(onClick=onBack){Text("← Back",color=Cyan)};Text(if(signup)"Create your account" else "Welcome back",fontSize=28.sp,fontWeight=FontWeight.Bold);Text("Sign in to sync your learning with Zenvora.",color=Muted)
 OutlinedTextField(email,{email=it},label={Text("Email address")},singleLine=true,modifier=Modifier.fillMaxWidth());OutlinedTextField(password,{password=it},label={Text("Password")},singleLine=true,modifier=Modifier.fillMaxWidth())
 Button(onClick={if(email.isBlank()||password.length<6)message="Enter a valid email and password of at least 6 characters." else{busy=true;scope.launch{try{val result=withContext(Dispatchers.IO){SupabaseAuth.authenticate(email,password,signup)};busy=false;if(result.first){if(result.second)onSignedIn(email) else message="Check your email to confirm your account, then log in."}else message=result.third}catch(e:Exception){busy=false;message=e.message?: "Could not connect. Try again."}}}},enabled=!busy,modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Cyan,contentColor=Navy)){Text(if(busy)"Please wait…" else if(signup)"Create account" else "Log in")}
 TextButton(onClick={signup=!signup;message=""}){Text(if(signup)"Already registered? Log in" else "New to Zenvora? Create account",color=Cyan)};if(message.isNotBlank())Text(message,color=Muted)
}}
private object SupabaseAuth{
 private const val URL="https://hovhakjqwqpxhemshtls.supabase.co";private const val KEY="sb_publishable_6cqUFpNid5mhd24Q3hNR1A_ntqScNau";private val client=OkHttpClient()
 fun authenticate(email:String,password:String,signup:Boolean):Triple<Boolean,Boolean,String>{
  val endpoint=if(signup)"$URL/auth/v1/signup" else "$URL/auth/v1/token?grant_type=password"
  val body=JSONObject().put("email",email).put("password",password).toString().toRequestBody("application/json".toMediaType())
  val req=Request.Builder().url(endpoint).addHeader("apikey",KEY).addHeader("Content-Type","application/json").post(body).build()
  client.newCall(req).execute().use{res->val data=res.body?.string().orEmpty();val json=runCatching{JSONObject(data)}.getOrNull()
   if(res.isSuccessful)return Triple(true,json?.optString("access_token").orEmpty().isNotBlank(), "")
   return Triple(false,false,json?.optString("msg")?.takeIf{it.isNotBlank()}?:json?.optString("message")?.takeIf{it.isNotBlank()}?:"Authentication failed. Check your details.")
  }
 }
}


@Composable private fun BannerAd(){
 AndroidView(
  modifier=Modifier.fillMaxWidth().height(50.dp),
  factory={context->
   AdView(context).apply{
    setAdSize(AdSize.BANNER)
    adUnitId="ca-app-pub-9279720382201715/6334854268"
    loadAd(AdRequest.Builder().build())
   }
  }
 )
}


private data class TutorMessage(val role:String,val content:String)

@Composable private fun AiTutorScreen(){
 val context=androidx.compose.ui.platform.LocalContext.current
 val scope= rememberCoroutineScope()
 var question by remember{mutableStateOf("")}
 var busy by remember{mutableStateOf(false)}
 var error by remember{mutableStateOf("")}
 val messages= remember{mutableStateListOf<TutorMessage>().apply{
  add(TutorMessage("assistant","Hi! I'm your Zenvora AI Tutor. Ask me anything about CapCut, KineMaster, Alight Motion, Node Video, Filmora or DaVinci Resolve. You can ask in English or Hinglish."))
 }}
 Column(Modifier.fillMaxSize().padding(horizontal=16.dp, vertical=12.dp)){
  Text("AI TUTOR",color=Cyan,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
  Text("Learn with Grok",fontSize=26.sp,fontWeight=FontWeight.ExtraBold)
  Text("Your personal creative-learning assistant",color=Muted,fontSize=12.sp)
  Spacer(Modifier.height(12.dp))
  LazyColumn(Modifier.weight(1f).fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(vertical=8.dp)){
   items(messages){msg->
    Row(Modifier.fillMaxWidth(),horizontalArrangement=if(msg.role=="user")Arrangement.End else Arrangement.Start){
     Surface(color=if(msg.role=="user")Color(0xFF123B4A) else Panel,shape=RoundedCornerShape(16.dp),border=BorderStroke(1.dp,Color(0xFF29435C)),modifier=Modifier.fillMaxWidth(0.88f)){
      Text(msg.content,Modifier.padding(13.dp),color=Ink,fontSize=14.sp)
     }
    }
   }
   if(busy)item{Text("Grok is thinking…",color=Cyan,fontSize=12.sp)}
  }
  if(error.isNotBlank())Text(error,color=Color(0xFFFF8A80),fontSize=12.sp)
  Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
   OutlinedTextField(value=question,onValueChange={question=it},modifier=Modifier.weight(1f),placeholder={Text("Ask your AI tutor…")},maxLines=4,shape=RoundedCornerShape(14.dp))
   IconButton(onClick={
    val prompt=question.trim()
    if(prompt.isNotEmpty()&&!busy){
     messages.add(TutorMessage("user",prompt));question="";error="";busy=true
     scope.launch{
      try{
       val answer=withContext(Dispatchers.IO){
        val history=org.json.JSONArray()
        messages.dropLast(1).takeLast(10).forEach{history.put(JSONObject().put("role",it.role).put("content",it.content))}
        val body=JSONObject().put("message",prompt).put("course","Zenvora AI Tutor — video editing and creative skills").put("history",history).toString().toRequestBody("application/json".toMediaType())
        val request=Request.Builder().url("https://brohack67-ai-tutor-production.up.railway.app/api/chat").post(body).addHeader("Content-Type","application/json").build()
        OkHttpClient().newCall(request).execute().use{response->
         val payload=response.body?.string().orEmpty()
         val json=runCatching{JSONObject(payload)}.getOrNull()
         if(!response.isSuccessful)throw IllegalStateException(json?.optString("error")?.takeIf{it.isNotBlank()}?:"AI service returned HTTP "+response.code)
         json?.optString("reply")?.takeIf{it.isNotBlank()}?:throw IllegalStateException("AI returned an empty answer.")
        }
       }
       messages.add(TutorMessage("assistant",answer))
      }catch(e:Exception){error=e.message?: "Couldn't connect to AI Tutor. Please try again."}
      finally{busy=false}
     }
    }
   },enabled=!busy&&question.isNotBlank()){
    Icon(Icons.Filled.Send,null,tint=Cyan)
   }
  }
  Text("AI answers may need verification. Never share passwords or private information.",color=Muted,fontSize=10.sp,modifier=Modifier.padding(top=6.dp))
 }
}
