# File Processor (DSP Test)

## Steps To Take Before Running The App

1. Visit [this link](https://developers.google.com/drive/api/quickstart/java), then follow the guides to enable API and create Credentials
2. Once you have finished download the credentials, store it inside `src/main/resources/credentials.json`
3. Prepare the path to images intended to upload for the API request
4. Open [this link](https://github.com/tesseract-ocr/tessdata)
5. Download `chi_tra.traineddata` and `eng.traineddata` and put it to `tessdata` in resources folder

## How to Build The Program

- Open a terminal
- Change the working directory into the current project directory
- Execute `mvn clean install` in your terminal

## How to Run The Program

- Open a terminal
- Change the working directory into the current project directory
- Execute `mvn spring-boot:run` in your terminal

## File Outputs

![ImageWithWords1](https://user-images.githubusercontent.com/46013258/223324380-4a534a1c-8b5b-47d7-b11d-9eca6e66870f.png)

![ImageWithWords2](https://user-images.githubusercontent.com/46013258/223324523-f0a6447b-ed44-4e8b-8d2f-3fd8b2a4547b.png)

![ImageWithWords3](https://user-images.githubusercontent.com/46013258/223324624-d94e719c-d5b9-40b0-86ba-13648863887a.png)

![ImageWithWords4](https://user-images.githubusercontent.com/46013258/223324695-4523ddda-6a7f-4042-9f11-a8c7be29dfd4.png)
