parallelSearch = (keywords, cb) ->
  out = []
  await 
    for k,i in keywords
      search k, defer out[i]
  cb out