import React, { Component } from 'react';
import './QRBarcodeScanner.css';

class QRBarcodeScanner extends Component {
  constructor(props) {
    super(props);
    this.state = {
      scanning: false,
      result: '',
      cameraMode: 'environment',
      errorMsg: '',
    };
    this.scanner = null;
    this.scannerId = 'qr-reader';
  }

  componentDidMount() {
    // html5-qrcode를 동적 import로 로드 (process 오류 방지)
    setTimeout(() => {
      this.loadScanner();
    }, 200);
  }

  componentWillUnmount() {
    this.stopScanner();
  }

  loadScanner = () => {
    try {
      // 동적 require로 process 오류 우회
      var Html5QrcodeScanner = require('html5-qrcode').Html5QrcodeScanner;
      this.Html5QrcodeScanner = Html5QrcodeScanner;
      this.initScanner();
    } catch (e) {
      console.error('라이브러리 로드 실패:', e);
      this.setState({ errorMsg: '스캐너 로드 실패: ' + e.message });
    }
  };

  initScanner = () => {
    if (!this.Html5QrcodeScanner) return;

    if (this.scanner) {
      this.scanner.clear().catch(function() {});
      this.scanner = null;
    }

    var config = {
      fps: 10,
      qrbox: { width: 250, height: 250 },
      videoConstraints: {
        facingMode: this.state.cameraMode,
      },
      rememberLastUsedCamera: false,
    };

    try {
      this.scanner = new this.Html5QrcodeScanner(
        this.scannerId,
        config,
        false
      );
      this.scanner.render(
        this.onScanSuccess,
        this.onScanError
      );
      this.setState({ scanning: true, errorMsg: '' });
    } catch (e) {
      console.error('스캐너 초기화 실패:', e);
      this.setState({ errorMsg: '스캐너 초기화 실패: ' + e.message });
    }
  };

  onScanSuccess = (decodedText) => {
    this.setState({ result: decodedText });

    // 안드로이드 웹뷰로 결과 전달
    if (window.Android && typeof window.Android.onScanResult === 'function') {
      window.Android.onScanResult(decodedText);
    }

    if (this.props.onScanResult) {
      this.props.onScanResult(decodedText);
    }
  };

  onScanError = (errorMessage) => {
    console.warn('Scan:', errorMessage);
  };

  stopScanner = () => {
    if (this.scanner) {
      this.scanner.clear().catch(function() {});
      this.scanner = null;
    }
    this.setState({ scanning: false });
  };

  handleRescan = () => {
    this.setState({ result: '', scanning: false }, () => {
      setTimeout(() => this.initScanner(), 300);
    });
  };

  toggleCamera = () => {
    var newMode =
      this.state.cameraMode === 'environment' ? 'user' : 'environment';
    this.stopScanner();
    this.setState({ cameraMode: newMode }, () => {
      setTimeout(() => this.initScanner(), 400);
    });
  };

  render() {
    var result = this.state.result;
    var cameraMode = this.state.cameraMode;
    var errorMsg = this.state.errorMsg;

    return (
      <div className="scanner-container">
        <div className="scanner-controls">
          <button className="ctrl-btn" onClick={this.toggleCamera}>
            {cameraMode === 'environment' ? '전면 카메라로 전환' : '후면 카메라로 전환'}
          </button>
          {result && (
            <button className="ctrl-btn rescan-btn" onClick={this.handleRescan}>
              다시 스캔
            </button>
          )}
        </div>

        {/* 에러 표시 */}
        {errorMsg ? (
          <div className="error-box">
            <p>{errorMsg}</p>
            <button className="ctrl-btn" onClick={this.loadScanner}>
              재시도
            </button>
          </div>
        ) : (
          /* 스캐너 렌더링 영역 */
          <div id={this.scannerId} className="scanner-view"></div>
        )}

        {/* 스캔 결과 */}
        {result && (
          <div className="result-box">
            <h3>스캔 결과</h3>
            <p className="result-value">{result}</p>
            <button
              className="copy-btn"
              onClick={() => {
                if (navigator.clipboard) {
                  navigator.clipboard.writeText(result);
                }
              }}
            >
              복사
            </button>
          </div>
        )}
      </div>
    );
  }
}

export default QRBarcodeScanner;
