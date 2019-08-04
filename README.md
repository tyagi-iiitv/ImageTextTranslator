# ImageTextTranslator

This android application takes an image of the English text and extracts text from it using Tesseract API. Then it translated that to Hindi. This was built during a Hackathon at Indian Institute of Information Technology, Vadodara.  


# Usage Instructions

- This app requires Tesseract to work, so you need to create a folder named **SimpleAndroidOCR** inside the internal storage of your android device. Then, inside this folder, create a new directory named **tessdate**. Now copy the file [eng.traineddata](https://github.com/tesseract-ocr/tessdata/blob/master/eng.traineddata) inside the **tessdata** folder.

- Make sure you've granted all the permissions to the app.

- Make sure you're connected to the internet.

# Features and usage example

- On the first screen, clink on **TAKE PHOTO** on top of your screen.

- Then you can click picture on any english text. Make sure you're close enough while clicking as the word should be clear.

- Now a crop screen will open, you can crop **one word** which you want to translate and then click **CROP** at the bottom of the screen.

- The extracted should appear in the text box on the next screen. Don't worry if it doesn't, it sometimes won't. You can manually enter the text at this point of time. 

- Then click on Translate and the corresponding Hindi text will appear in the text box below.

- This is mainly for practicing how to connect Tesseract and Yandex Translate APIs to an android application.
