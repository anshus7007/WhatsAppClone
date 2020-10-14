package com.anshu.whatsappmessenger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.model.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(context:Context, mUsers:List<Users>,
isThatCheck: Boolean):RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{

    private var context=context
    private var mUser=mUsers
    private var isThatCheck= isThatCheck

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item_search_layout,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        var user= mUser[position]
        holder.usernameSearch.text=user!!.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImage)

    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class UserViewHolder(item: View): RecyclerView.ViewHolder(item)
    {
         var profileImage: CircleImageView=item.findViewById(R.id.profile_image_search)
         var usernameSearch:TextView= item.findViewById(R.id.usernameSearch)
        var imageOnline: CircleImageView=item.findViewById(R.id.image_online)
        var imageOffline: CircleImageView=item.findViewById(R.id.image_offline)
        var lastMessage: TextView=item.findViewById(R.id.message_last)

    }
}