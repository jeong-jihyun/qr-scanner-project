# QR/Barcode Scanner
## React 16 + Node 12 + Android WebView

---

## 📁 프로젝트 구조

```
qr-scanner-project/
├── react-app/                        # React 웹앱
│   ├── .env                          # 환경변수 설정
│   ├── package.json
│   ├── public/index.html
│   └── src/
│       ├── index.js
│       ├── App.js
│       ├── App.css
│       ├── QRBarcodeScanner.js
│       └── QRBarcodeScanner.css
│
└── android-app/                      # Android 프로젝트
    ├── gradlew.bat                   # Windows 빌드
    ├── gradlew                       # Mac/Linux 빌드
    ├── settings.gradle
    ├── build.gradle
    ├── gradle.properties
    ├── local.properties              # SDK 경로 설정
    ├── gradle/wrapper/
    │   └── gradle-wrapper.properties
    └── app/
        ├── build.gradle
        ├── proguard-rules.pro
        └── src/main/
            ├── AndroidManifest.xml
            ├── java/com/qrscanner/
            │   └── MainActivity.java
            └── res/
                ├── layout/activity_main.xml
                ├── drawable/ic_launcher.xml
                ├── xml/network_security_config.xml
                └── values/
                    ├── strings.xml
                    └── styles.xml
```

---

## 🚀 React 앱 실행

```cmd
cd react-app
npm install
npm start
```

---

## 📱 모바일 접속 방법

카메라는 HTTPS 필수입니다.
아래 두 가지 방법 중 하나를 선택하세요.

---

## 방법 1. mkcert (권장 - ngrok 불필요)

로컬 HTTPS 인증서를 생성해서 ngrok 없이 바로 접속합니다.

### Step 1. PC IP 확인
```cmd
ipconfig
```
Wi-Fi 항목의 IPv4 주소 확인 (예: 192.168.45.223)

### Step 2. chocolatey 설치 (관리자 PowerShell)
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

### Step 3. mkcert 설치
```cmd
choco install mkcert
```

### Step 4. 인증서 생성
```cmd
cd react-app
mkcert -install
mkcert 192.168.45.223
```
※ 192.168.45.223 → 본인 PC IP로 변경

생성된 파일:
```
192.168.45.223.pem
192.168.45.223-key.pem
```

### Step 5. .env 파일 수정
```
PORT=3001
HTTPS=true
SSL_CRT_FILE=192.168.45.223.pem
SSL_KEY_FILE=192.168.45.223-key.pem
DANGEROUSLY_DISABLE_HOST_CHECK=true
```
※ 파일명은 본인 IP에 맞게 변경

### Step 6. React 실행
```cmd
npm start
```
브라우저에서 https://192.168.45.223:3001 접속 확인

### Step 7. MainActivity.java 수정
```java
private static final String DEFAULT_URL = "https://192.168.45.223:3001/";
```
※ 192.168.45.223 → 본인 PC IP로 변경

### Step 8. Android 빌드
Android Studio에서:
1. Build → Clean Project
2. Build → Rebuild Project
3. Run(▶)

---

## 방법 2. ngrok (외부 네트워크에서도 접속 가능)

### Step 1. ngrok 설치
```cmd
npm install -g ngrok
```

### Step 2. ngrok 회원가입 및 토큰 등록
1. https://dashboard.ngrok.com/signup 회원가입
2. https://dashboard.ngrok.com/get-started/your-authtoken 토큰 복사
3. 아래 명령어 실행
```cmd
ngrok config add-authtoken 복사한토큰
```

### Step 3. React 실행
```cmd
cd react-app
npm start
```

### Step 4. ngrok 실행 (별도 CMD)
```cmd
ngrok http 3001
```
생성된 주소 확인:
```
Forwarding: https://xxxx.ngrok-free.dev → http://localhost:3001
```

### Step 5. MainActivity.java 수정
```java
private static final String DEFAULT_URL = "https://xxxx.ngrok-free.dev/";
```
※ ngrok 실행할 때마다 주소가 바뀌므로 재빌드 필요

### Step 6. Android 빌드
Android Studio에서:
1. Build → Clean Project
2. Build → Rebuild Project
3. Run(▶)

---

## ⚙️ local.properties SDK 경로 수정

```properties
# Windows 예시
sdk.dir=C\:\\Users\\사용자이름\\AppData\\Local\\Android\\Sdk

# Mac 예시
sdk.dir=/Users/사용자이름/Library/Android/sdk
```

---

## 🔗 웹 → Android 통신

스캔 결과는 자동으로 Android로 전달됩니다:

```javascript
// React 코드 (자동 호출)
if (window.Android) {
    window.Android.onScanResult("스캔결과");
}
```

---

## ⚠️ 주의사항

| 항목 | 내용 |
|---|---|
| Node 버전 | 12.x |
| React 버전 | 16.14.0 |
| Android 최소 | API 16 (Android 4.1) |
| 카메라 | HTTPS 필수 |
| mkcert IP | PC IP 바뀌면 인증서 재생성 필요 |
| ngrok | 실행할 때마다 URL 변경됨 (유료시 고정) |
