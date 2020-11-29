import React from "react";
import BasicPage from "./BasicPage";
import EmailAvatar from "../EmailAvatar";
import UnauthorizedPage from "./nonIdealStates/UnauthorizedPage";
import {
    Alignment,
    Button,
    Card,
    Icon,
    Intent, NavbarDivider,
    NavbarGroup,
    NonIdealState,
    Spinner,
    Tag
} from "@blueprintjs/core";

import {gql, useQuery} from "@apollo/client";
import moment from "moment";

const GET_USER_REPOSITORIES = gql`
    query {
        userMe {
            repositories {
                path {
                    name
                    namespace
                    type
                }
                accessPermission
                commits(pagination: {
                    skip: 0, limit: 1
                }) {
                    author {
                        name
                        email
                        date
                    }
                    subject
                    refs {
                        commit
                    }
                }
            }
        }
    }
`

export default () => {
    if (!localStorage.getItem("token")) {
        return <UnauthorizedPage/>
    }

    const {data, loading, error} = useQuery(GET_USER_REPOSITORIES);

    const NewRepositoryButton = () => {
        return <Button
            icon="plus"
            intent={Intent.SUCCESS}
        >New repository</Button>
    }

    const CommitInfo = (props: any) => {
        if (!props.commit) {
            return <div>Repository is empty</div>
        }

        return <div style={{
            display: "flex",
            alignItems: "center"
        }}>
            <EmailAvatar size={24} email={props.commit.author.email}/>
            <div style={{ padding: 4 }}/>
            <b>{props.commit.author.name}</b>
            <div style={{ padding: 4 }}/>
            {props.commit.subject}
            <div style={{ padding: 4 }}/>
            <Tag>{props.commit.refs.commit.substr(0, 7)}</Tag>
            <NavbarDivider/>
            <div>{moment(props.commit.author.date * 1000).fromNow()}</div>
        </div>
    }

    return <BasicPage>
        {!data ? <Spinner/> : <div className="flex-column">
            <div>
                <NavbarGroup align={Alignment.LEFT}>
                    <h1>Your repositories</h1>
                </NavbarGroup>
                <NavbarGroup align={Alignment.RIGHT}>
                    <NewRepositoryButton/>
                </NavbarGroup>
            </div>
            <div>
                {data.userMe.repositories.length === 0 ? <NonIdealState
                    icon="document"
                    title="Nothing here"
                    description="You don`t have any repository yet"
                    action={<NewRepositoryButton/>}
                /> : <div>
                    {(data.userMe.repositories as any[]).map(value => {
                        return <Card elevation={1} style={{
                            display: "flex",
                            justifyContent: "space-between",
                            marginTop: 12
                        }}>
                            <div style={{
                                display: "flex",
                                alignItems: "center"
                            }}>
                                <Icon icon="git-repo"/>
                                <div style={{ padding: 4 }}/>
                                <div>{`${value.path.namespace} / ${value.path.name}`}</div>
                                <div style={{ padding: 4 }}/>
                                <div><Tag>{value.accessPermission.toLowerCase()}</Tag></div>
                            </div>
                            <CommitInfo commit={value.commits[0]}/>
                        </Card>
                    })}
                </div>}
            </div>
        </div>}
    </BasicPage>
}
