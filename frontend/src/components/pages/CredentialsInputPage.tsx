import React from "react";
import {Card, HTMLDivProps} from "@blueprintjs/core";

import logo from "../../images/logoWithText.svg";
import {Link} from "react-router-dom";

interface CredentialsInputPageProps extends HTMLDivProps {
    description: string;
    callout?: React.ReactNode;
}

export default (props: CredentialsInputPageProps) => {
    return <div className="credentials-input-page-container">
        <Link to="/">
            <img alt="logo" src={logo} width={42}/>
        </Link>
        {props.callout && <div className="credentials-input-page-callout">{props.callout}</div>}
        <h3>{props.description}</h3>
        <Card className="credentials-input-page-card" elevation={1}>
            {props.children}
        </Card>
    </div>
}
