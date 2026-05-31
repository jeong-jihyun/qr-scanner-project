import React, { Component } from 'react';
import QRBarcodeScanner from './QRBarcodeScanner';
import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      scanHistory: [],
    };
  }

  handleScanResult = (result) => {
    var newEntry = {
      id: Date.now(),
      value: result,
      time: new Date().toLocaleTimeString(),
    };
    this.setState(function(prevState) {
      return {
        scanHistory: [newEntry].concat(prevState.scanHistory).slice(0, 10),
      };
    });
  };

  handleClear = () => {
    this.setState({ scanHistory: [] });
  };

  render() {
    var scanHistory = this.state.scanHistory;

    return (
      <div className="app">
        <header className="app-header">
          <h1>QR / 바코드 스캐너</h1>
          <p>React 16 + Node 12 + 클래스 컴포넌트</p>
        </header>

        <main className="app-main">
          <QRBarcodeScanner onScanResult={this.handleScanResult} />

          <section className="history-section">
            <div className="history-header">
              <h2>스캔 기록</h2>
              {scanHistory.length > 0 && (
                <button className="clear-btn" onClick={this.handleClear}>
                  초기화
                </button>
              )}
            </div>

            {scanHistory.length === 0 ? (
              <p className="empty-msg">스캔 결과가 여기에 표시됩니다</p>
            ) : (
              <ul className="history-list">
                {scanHistory.map(function(item) {
                  return (
                    <li key={item.id} className="history-item">
                      <span className="history-value">{item.value}</span>
                      <span className="history-time">{item.time}</span>
                    </li>
                  );
                })}
              </ul>
            )}
          </section>
        </main>
      </div>
    );
  }
}

export default App;
