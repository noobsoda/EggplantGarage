import Router from "./Router";
import { ThemeProvider } from "styled-components";
import theme from "./styles/theme.js";
import GlobalStyle from "./styles/globalStyle";
import "./styles/global.css";

function App() {
  return (
    <div
      className="App"
      style={{
        height: "100vh",
        width: "100%",
        minWidth: "360px",
        maxWidth: "435px",
        overflow: "hidden",
      }}
    >
      <GlobalStyle />
      <ThemeProvider theme={theme}>
        <Router />
      </ThemeProvider>
    </div>
  );
}

export default App;
