import React from 'react';
import Helmet from "react-helmet";
import ReactDOM from 'react-dom';
import App from './App';

import '@blueprintjs/core/lib/css/blueprint.css'
import '@blueprintjs/icons/lib/css/blueprint-icons.css'
import './index.css';

import logo from "./images/favicon.png"

ReactDOM.render(
    <React.StrictMode>
        <Helmet>
            <link type="image/png" rel="icon" href={logo}/>
        </Helmet>
        <App/>
    </React.StrictMode>,
    document.getElementById('root')
);
