import React from "react";
import EmailAvatar from "./EmailAvatar";
import {NavbarDivider, Tag} from "@blueprintjs/core";
import moment from "moment";

export interface GitCommit {
    author: {
        name: string;
        email: string;
        date: number;
    }
    refs: {
        commit: string;
    }
    subject: string;
}

export enum CommitInfoType {
    SHORT,
    FULL
}

interface CommitInfoProps {
    commit: GitCommit;
    type: CommitInfoType;
}

export default (props: CommitInfoProps) => {
    return <div style={{
        display: "flex",
        justifyContent: props.type === CommitInfoType.SHORT ? "center" : "space-between",
        alignItems: "center",
        padding: props.type === CommitInfoType.SHORT ? 0 : 6
    }}>
        <div style={{
            display: "flex",
            alignItems: "center"
        }}>
            <EmailAvatar size={24} email={props.commit.author.email}/>
            <div style={{ padding: 4 }}/>
            <b>{props.commit.author.name}</b>
            <div style={{ padding: 4 }}/>
            {props.commit.subject}
        </div>
        <div style={{
            display: "flex",
            alignItems: "center"
        }}>
            <div style={{ padding: 4 }}/>
            <Tag>{props.commit.refs.commit.substr(0, 7)}</Tag>
            <NavbarDivider/>
            <div style={props.type === CommitInfoType.FULL ? {
                width: 100
            } : {}}>{moment(props.commit.author.date * 1000).fromNow()}</div>
        </div>
    </div>
}
