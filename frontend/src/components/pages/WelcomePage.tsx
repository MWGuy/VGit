import React, {useState} from "react";
import BasicPage from "./BasicPage";
import UnauthorizedPage from "./nonIdealStates/UnauthorizedPage";
import {Alignment, Button, Card, Icon, Intent, NavbarGroup, NonIdealState, Spinner, Tag} from "@blueprintjs/core";

import {gql, useQuery} from "@apollo/client";
import CommitInfo, {CommitInfoType} from "../CommitInfo";
import {Link} from "react-router-dom";
import NewRepositoryOverlay from "../dialogs/NewRepositoryDialog";

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

    const {data} = useQuery(GET_USER_REPOSITORIES);

    const NewRepositoryButton = () => {
        const [overlayOpened, setOverlayOpened] = useState<boolean>(false);

        return <React.Fragment>
            <NewRepositoryOverlay isOpen={overlayOpened} onClose={() => setOverlayOpened(false)}/>
            <Button
                icon="plus"
                intent={Intent.SUCCESS}
                onClick={() => setOverlayOpened(!overlayOpened)}
            >New repository</Button>
        </React.Fragment>
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
                                <Link to={`/${value.path.namespace}/${value.path.name}`}>{`${value.path.namespace} / ${value.path.name}`}</Link>
                                <div style={{ padding: 4 }}/>
                                <div><Tag>{value.accessPermission.toLowerCase()}</Tag></div>
                            </div>
                            {value.commits[0] ? <CommitInfo type={CommitInfoType.SHORT} commit={value.commits[0]}/> : <div>Repository is empty</div>}
                        </Card>
                    })}
                </div>}
            </div>
        </div>}
    </BasicPage>
}
