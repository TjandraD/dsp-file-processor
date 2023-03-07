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
