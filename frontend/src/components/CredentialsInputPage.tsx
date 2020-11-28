import React from "react";
import {Card, HTMLDivProps, Icon} from "@blueprintjs/core";

interface CredentialsInputPageProps extends HTMLDivProps {
    description: string;
    callout?: React.ReactNode;
}

export default (props: CredentialsInputPageProps) => {
    return <div className="credentials-input-page-container">
        <Icon icon="git-branch" iconSize={42}/>
        {props.callout && <div className="credentials-input-page-callout">{props.callout}</div>}
        <h3>{props.description}</h3>
        <Card className="credentials-input-page-card" elevation={1}>
            {props.children}
        </Card>
    </div>
}
