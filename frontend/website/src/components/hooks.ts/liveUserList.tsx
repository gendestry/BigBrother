import { useEffect, useState } from "react";
import { Tables } from "../../supabase/supabase";
import { PostgrestError } from "@supabase/supabase-js";
import { supabaseClient } from "../../supabase/supabaseClient";

let usersBuffer: Tables<"users">[] = [];

export const useLiveUserList = () => {
    const [users, setUsers] = useState<Tables<"users">[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<Error | PostgrestError>();

    useEffect(() => {
        usersBuffer = [...users];
    }, [users]);

    const updateUsers = () => {
        setUsers(users);
    };

    supabaseClient
        .channel("schema-db-changes")
        .on<Tables<"users">>(
            "postgres_changes",
        {
            event: "UPDATE",
            schema: "public",
        },
        (payload) => {
            console.log("here");
            if(payload.errors) {
                setError(new Error(payload.errors[0]));
                setUsers([]);
                return;
            }
            
            for (let i = 0; i < usersBuffer.length; i++) {
                if(usersBuffer[i].id === payload.new.id) {
                    usersBuffer[i] = payload.new;
                }
            }
            setUsers([...usersBuffer]);
        }).on<Tables<"users">>(
            "postgres_changes",
        {
            event: "UPDATE",
            schema: "public",
        },
        (payload) => {
            if(payload.errors) {
                setError(new Error(payload.errors[0]));
                setUsers([]);
                return;
            }
            
            setUsers(usersBuffer.filter((value) => value.id != payload.new.id));
        });

    useEffect(() => {
        setLoading(true);
        supabaseClient.from("users")
            .select()
            .then((response) => {
                if(response.error) {
                    setError(response.error);
                }
                else {
                    setUsers(response.data);
                    usersBuffer = response.data;
                }
                setLoading(false);
            });
    }, []);

    return { users, loading, error, updateUsers };
};