import React from "react";
import BasicPage from "../BasicPage";
import {gql, useQuery} from "@apollo/client";
import {Breadcrumbs, Card, InputGroup, Menu, MenuItem, NonIdealState, Spinner} from "@blueprintjs/core";
import NotFoundPage from "../nonIdealStates/NotFoundPage";
import RepositoryCommitsPage from "./RepositoryCommitsPage";
import { useHistory } from "react-router-dom";

export interface RepositoryPath {
    namespace: string;
    name: string;
}

export interface RepositorySubpageProps {
    path: RepositoryPath;
    info?: string;
}

export interface RepositorySubpageItem {
    path: string;
    name: string;
    icon: string;
    component: (props: RepositorySubpageProps) => React.ReactNode;
}

const GET_REPOSITORY_BASIC_DATA = gql`
    query ($name: String!, $namespace: String!) {
        repositoryByPath(path: {
            name: $name, namespace: $namespace
        }) {
            path {
                name
                namespace
                type
            }
            accessPermission
            description
        }
    }
`

const repositorySubpages: RepositorySubpageItem[] = [
    {
        path: "commit",
        name: "Commits",
        icon: "git-commit",
        component: (props: RepositorySubpageProps) => <RepositoryCommitsPage {...props}/>
    }
];

function getCurrentRepositorySubpageItem(page: string): RepositorySubpageItem | undefined {
    let item: RepositorySubpageItem | undefined = undefined;

    for (const repositorySubpage of repositorySubpages) {
        if (repositorySubpage.path === page) {
            item = repositorySubpage;
        }
    }

    return item;
}

function getCurrentRepositorySubpageComponentOrGetFallback(item: RepositorySubpageItem | undefined, props: RepositorySubpageProps): React.ReactNode {
    if (!item) {
        return <NonIdealState icon="error" title="Not found" description="Can`t find requested subpage"/>
    }

    return item.component(props);
}

export default (props: any) => {
    const history = useHistory();
    const {data, loading, error} = useQuery(GET_REPOSITORY_BASIC_DATA, {
        variables: {
            namespace: props.match.params.namespace,
            name: props.match.params.name
        }
    });

    if (error || (data && !data.repositoryByPath)) {
        return <NotFoundPage/>
    }

    if (loading || !data) {
        return <BasicPage>
            <Spinner/>
        </BasicPage>
    }

    const item = getCurrentRepositorySubpageItem(props.match.params.page);
    const items: any[] = [
        {
            href: "/" + data.repositoryByPath.path.namespace,
            text: data.repositoryByPath.path.namespace,
            icon: data.repositoryByPath.path.type === "USER" ? "user" : "people"
        },
        {
            icon: "git-repo",
            href: "/" + data.repositoryByPath.path.namespace + "/" + data.repositoryByPath.path.name,
            text: data.repositoryByPath.path.name,
            current: item === undefined
        }
    ];

    if (item) {
        items.push({
            icon: item.icon,
            text: item.name,
            href: "/" + data.repositoryByPath.path.namespace + "/" + data.repositoryByPath.path.name + "/" + item.path,
            current: true
        });
    }

    return <BasicPage>
        <div style={{
            display: "flex",
            flexDirection: "row"
        }}>
            <Card style={{
                width: 300,
                marginRight: 25,
                padding: 0
            }}>
                <Menu>
                    {repositorySubpages.map(value => {
                        // @ts-ignore
                        return <MenuItem icon={value.icon}
                            text={value.name}
                            active={props.match.params.page === value.path}
                            onClick={() => {
                                history.push("/" + props.match.params.namespace + "/" + props.match.params.name + "/" + value.path)
                            }}
                        />
                    })}
                </Menu>
            </Card>
            <div style={{
                width: "100%"
            }}>
                <Card style={{
                    marginBottom: 12
                }}>
                    <Breadcrumbs items={items}/>
                </Card>
                <InputGroup fill readOnly style={{
                    marginBottom: 12
                }} value={window.location.protocol + "//" + window.location.host + "/" + data.repositoryByPath.path.namespace + "/" + data.repositoryByPath.path.name + ".git"}/>
                <Card>
                    {getCurrentRepositorySubpageComponentOrGetFallback(item, {
                        path: {
                            namespace: props.match.params.namespace,
                            name: props.match.params.name
                        },
                        info: props.match.params.info
                    })}
                </Card>
            </div>
        </div>
    </BasicPage>
}
