import React, {useCallback, useEffect, useState} from "react";
import {RepositorySubpageProps} from "./RepositoryPage";
import {gql} from "@apollo/client";
import {Button, Intent, NonIdealState, Spinner, Tag} from "@blueprintjs/core";
import {apolloClient} from "../../../App";
import CommitInfo, {CommitInfoType, GitCommit} from "../../CommitInfo";

const GET_COMMITS = gql`
    query($path: RepositoryPathInput!, $pagination: PaginationInput!) {
        repositoryByPath(path: $path) {
            commits(pagination: $pagination) {
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
`

export default (props: RepositorySubpageProps) => {
    const [items, setItems] = useState<GitCommit[]>([]);
    const [hasMore, setHasMore] = useState<boolean>(true);
    const [loading, setLoading] = useState<boolean>(true);

    if (props.info) {
        return <NonIdealState icon="git-commit" title="Not found" description={<div>Can`t find commit <Tag>{props.info.substr(0, 7)}</Tag></div>}/>
    }

    const next = useCallback(() => {
        setLoading(true);
        apolloClient.query({
            query: GET_COMMITS,
            variables: {
                path: props.path,
                pagination: {
                    skip: items.length,
                    limit: 20
                }
            }
        }).then(res => {
            const response = res.data.repositoryByPath.commits as GitCommit[];
            if (response.length < 20) {
                setHasMore(false);
            }

            setItems(items.concat(response));
            setLoading(false);
        });
    }, [setLoading, items, setHasMore, setItems, props]);

    useEffect(() => {
        if (items.length === 0 && hasMore) {
            next();
        }
    }, [items, hasMore, next]);

    return <div>
        {items.length === 0 && !hasMore && <NonIdealState icon="git-repo" title="Nothing here..." description="This repository is empty"/>}
        {items.map(value => <CommitInfo type={CommitInfoType.FULL} commit={value}/>)}
        {loading && <Spinner/>}
        {hasMore && !loading && <div style={{
            display: "flex",
            justifyContent: "center"
        }}>
            <Button icon="more" intent={Intent.PRIMARY} onClick={next}>Load more commits</Button>
        </div>}
    </div>
}
