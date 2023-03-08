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

![Chinese Output](https://user-images.githubusercontent.com/46013258/223734193-52fd6708-2af1-4c79-b5d3-8b00f993cca8.png)

![English Output](https://user-images.githubusercontent.com/46013258/223734382-62977c0a-3397-4305-8878-922994ddbeb1.png)
