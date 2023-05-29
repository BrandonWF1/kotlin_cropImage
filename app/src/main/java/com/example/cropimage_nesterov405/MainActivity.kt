@file:Suppress("DEPRECATION")

package com.example.cropimage_nesterov405

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private var userPic :ImageView? = null //ссылка на ImageView в макете
    private val storageRequest = 200 // целочисленный код, который будет использоваться при запросе разрешения
    private var storagePermission:Array<String>? = null // массив строк, который содержит разрешение на доступ к хранилищу
    private var click:Button? = null //ссылка на кнопку "Choose"
    override fun onCreate(savedInstanceState: Bundle?) { //инициализация переменных
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        click = findViewById(R.id.btnChoose)
        userPic = findViewById(R.id.set_profile_img)


        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        click!!.setOnClickListener { //установка обработчика нажатия на кнопку
            showImagePicDialog() // диалоговое окно для выбора источника изображения.
        }


    }

    //фукнция модального окна
    private fun showImagePicDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Pick Image From")
            .setPositiveButton("Gallery"){_,_-> //когда пользователь жмет кнопку - функция проверяет доступ
                if (!checkStoragePermission()){
                    requestStoragePermission() //повторный вызов функции для запроса разрешения в случае отрицаптельного ответа
                }else{
                    pickFromGallery() //функция выбора из галереи при положительном ответе
                }

            }
        builder.create().show() //метод который создает диалоговое окно и отображает его на экране
    }

    //функция проверки доступа к хранилищу, не забываем добавить permission в AndroidManifest
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )== PackageManager.PERMISSION_GRANTED

    }

    //функция непосредственного вызова галереи и обрезки для пользователя (из библиотеки Cropper)
    private fun pickFromGallery() {
        CropImage.activity().start(this@MainActivity) //запуск в контексте активности методом start()
    }

    //функция запроса разрешения к хранилищу
    private fun requestStoragePermission() {
        requestPermissions(storagePermission!!,storageRequest)
    }


    //Функция обратного вызова, обрабатывает результат запроса разрешения на доступ к хранилищу(при первом запуске)
    override fun onRequestPermissionsResult(
        requestCode: Int, // целочисленный код, который был передан при запросе разрешения.
        permissions: Array < String>, //массив строк, который содержит запрошенные разрешения
        grantResults: IntArray //массив целых чисел, который содержит результаты запроса разрешения
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == storageRequest){ //storageRequest = заданная нами ранее переменная
            if (grantResults.isNotEmpty()) { //проверяем, если массив больше нуля
                val writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED //проверка доступа
                if (writeStorageAccepted){
                    pickFromGallery() //функция для вызова галереи
                }else{
                    Toast.makeText(this,
                        "Please Enable Storage Permission", //вывод ошибки
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { //проверка совпадения с кодом запроса обрезки
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data) //получаем обрезанное изображение
            if (resultCode == RESULT_OK) { //проверка, если изображение обрезано успешно
                val resultUri: Uri = result.uri //обрезанное изображение в переменную
                Picasso.with(this).load(resultUri).into(userPic) //отрисовка с помощью библиотеки Picasso (по URI и идентификатору userPic)
            }
        }
    }


}